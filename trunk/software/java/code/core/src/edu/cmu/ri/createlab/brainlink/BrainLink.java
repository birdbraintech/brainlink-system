package edu.cmu.ri.createlab.brainlink;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import edu.cmu.ri.createlab.brainlink.commands.AuxSerialReceiveCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.AuxSerialTransmitCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.DACCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.DigitalInputCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.DigitalOutputCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.DisconnectCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.FullColorLEDCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.GetAccelerometerCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.GetAnalogInputsCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.GetBatteryVoltageCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.GetPhotoresistorCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.HandshakeCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.IRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.InitializeIRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.PWMCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.PlayStoredIRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.PlayToneCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.PrintStoredIRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.RecordIRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.SendRawIRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.SetAuxSerialConfigurationCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.SimpleIRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.StoreIRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.TurnOffIRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.TurnOffSpeakerCommandStrategy;
import edu.cmu.ri.createlab.device.CreateLabDevicePingFailureEventListener;
import edu.cmu.ri.createlab.device.CreateLabDeviceProxy;
import edu.cmu.ri.createlab.device.connectivity.BaseCreateLabDeviceConnectivityManager;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandExecutionQueue;
import edu.cmu.ri.createlab.serial.SerialDeviceNoReturnValueCommandExecutor;
import edu.cmu.ri.createlab.serial.SerialDeviceReturnValueCommandExecutor;
import edu.cmu.ri.createlab.serial.SerialPortEnumerator;
import edu.cmu.ri.createlab.serial.config.BaudRate;
import edu.cmu.ri.createlab.serial.config.CharacterSize;
import edu.cmu.ri.createlab.serial.config.FlowControl;
import edu.cmu.ri.createlab.serial.config.Parity;
import edu.cmu.ri.createlab.serial.config.SerialIOConfiguration;
import edu.cmu.ri.createlab.serial.config.StopBits;
import edu.cmu.ri.createlab.util.commandexecution.CommandExecutionFailureHandler;
import edu.cmu.ri.createlab.util.thread.DaemonThreadFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class BrainLink implements BrainLinkInterface
   {
   private static final Logger LOG = Logger.getLogger(BrainLink.class);

   public static final String APPLICATION_NAME = "BrainLink";
   private static final int DELAY_IN_SECONDS_BETWEEN_PEER_PINGS = 2;

   private final SerialDeviceCommandExecutionQueue commandQueue;
   private final String serialPortName;
   private final CreateLabSerialDeviceNoReturnValueCommandStrategy disconnectCommandStrategy = new DisconnectCommandStrategy();
   private final CreateLabSerialDeviceNoReturnValueCommandStrategy turnOffSpeakerCommandStrategy = new TurnOffSpeakerCommandStrategy();
   private final CreateLabSerialDeviceNoReturnValueCommandStrategy turnOffIRCommandStrategy = new TurnOffIRCommandStrategy();
   private final GetBatteryVoltageCommandStrategy getBatteryVoltageCommandStrategy = new GetBatteryVoltageCommandStrategy();
   private final GetAccelerometerCommandStrategy getAccelerometerCommandStrategy = new GetAccelerometerCommandStrategy();
   private final GetPhotoresistorCommandStrategy getPhotoresistorCommandStrategy = new GetPhotoresistorCommandStrategy();
   private final GetAnalogInputsCommandStrategy getAnalogInputsCommandStrategy = new GetAnalogInputsCommandStrategy();
   private final SerialDeviceNoReturnValueCommandExecutor noReturnValueCommandExecutor;
   private final SerialDeviceReturnValueCommandExecutor<Integer> integerReturnValueCommandExecutor;
   private final SerialDeviceReturnValueCommandExecutor<int[]> intArrayReturnValueCommandExecutor;
   private final SerialDeviceReturnValueCommandExecutor<Boolean> booleanReturnValueCommandExecutor;

   private final AtomicBoolean isConnected = new AtomicBoolean(false);
   private final BrainLinkPinger brainLinkPinger = new BrainLinkPinger();
   private final ScheduledExecutorService peerPingScheduler = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory("BrainLink.peerPingScheduler"));
   private final ScheduledFuture<?> peerPingScheduledFuture;
   private final Collection<CreateLabDevicePingFailureEventListener> createLabDevicePingFailureEventListeners = new HashSet<CreateLabDevicePingFailureEventListener>();
   private BrainLinkFileManipulator deviceFile;

   /**
    * Creates the <code>BrainLink</code> by checking all available serial ports and connecting to the first BrainLink it
    * finds.
    */
   public BrainLink()
      {
      this(null);
      }

   /**
    * Creates the <code>BrainLink</code> by attempting to connect to a BrainLink on the given serial port(s).  When
    * specifying multiple serial ports, they must be delimited by the platform's path separator character (e.g. ':' on
    * Mac OS X, ';' on Windows, etc.)  Note that if one or more serial ports is already specified as a system property
    * (e.g. by using the -Dgnu.io.rxtx.SerialPorts command line switch), then the serial port(s) specified in the
    * argument to this constructor are appended to the port names specified in the system property, and the system
    * property value is updated.
    *
    * @see SerialPortEnumerator#SERIAL_PORTS_SYSTEM_PROPERTY_KEY
    */
   @SuppressWarnings({"UseOfSystemOutOrSystemErr"})
   public BrainLink(final String userDefinedSerialPortNames)
      {
      if (userDefinedSerialPortNames != null && userDefinedSerialPortNames.trim().length() > 0)
         {
         LOG.debug("BrainLink.BrainLink(): processing user-defined serial port names...");

         // initialize the set of names to those specified in the argument to this constructor
         final StringBuilder serialPortNames = new StringBuilder(userDefinedSerialPortNames);

         // Now see if there are also serial ports already specified in the system property (e.g. via the -D command line switch).
         // If so, then those take precedence and the ones specified in the constructor argument will be appended
         final String serialPortNamesAlreadyInSystemProperty = System.getProperty(SerialPortEnumerator.SERIAL_PORTS_SYSTEM_PROPERTY_KEY, null);
         if (serialPortNamesAlreadyInSystemProperty != null && serialPortNamesAlreadyInSystemProperty.trim().length() > 0)
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("BrainLink.BrainLink(): Existing system property value = [" + serialPortNamesAlreadyInSystemProperty + "]");
               }
            serialPortNames.insert(0, System.getProperty("path.separator", ":"));
            serialPortNames.insert(0, serialPortNamesAlreadyInSystemProperty);
            }

         // now set the system property
         System.setProperty(SerialPortEnumerator.SERIAL_PORTS_SYSTEM_PROPERTY_KEY, serialPortNames.toString());
         if (LOG.isDebugEnabled())
            {
            LOG.debug("BrainLink.BrainLink(): System property [" + SerialPortEnumerator.SERIAL_PORTS_SYSTEM_PROPERTY_KEY + "] now set to [" + serialPortNames + "]");
            }
         }

      System.out.println("Connecting to BrainLink...this may take a few seconds...");
      LOG.debug("BrainLink.BrainLink(): creating the PseudoProxy...");
      final PseudoProxy pseudoProxy = new BrainLinkConnectivityManager().connect();
      isConnected.set(true);
      LOG.debug("BrainLink.BrainLink(): PseudoProxy created for serial port");

      this.commandQueue = pseudoProxy.getCommandExecutionQueue();
      this.serialPortName = pseudoProxy.getSerialPortName();

      final CommandExecutionFailureHandler commandExecutionFailureHandler =
            new CommandExecutionFailureHandler()
            {
            public void handleExecutionFailure()
               {
               brainLinkPinger.forceFailure();
               }
            };
      noReturnValueCommandExecutor = new SerialDeviceNoReturnValueCommandExecutor(commandQueue, commandExecutionFailureHandler);
      integerReturnValueCommandExecutor = new SerialDeviceReturnValueCommandExecutor<Integer>(commandQueue, commandExecutionFailureHandler);
      intArrayReturnValueCommandExecutor = new SerialDeviceReturnValueCommandExecutor<int[]>(commandQueue, commandExecutionFailureHandler);
      booleanReturnValueCommandExecutor = new SerialDeviceReturnValueCommandExecutor<Boolean>(commandQueue, commandExecutionFailureHandler);

      // schedule periodic peer pings
      peerPingScheduledFuture = peerPingScheduler.scheduleAtFixedRate(brainLinkPinger,
                                                                      DELAY_IN_SECONDS_BETWEEN_PEER_PINGS, // delay before first ping
                                                                      DELAY_IN_SECONDS_BETWEEN_PEER_PINGS, // delay between pings
                                                                      TimeUnit.SECONDS);
      }

   /** Returns the name of the serial port to which this BrainLink is connected. */
   @Override
   public String getPortName()
      {
      return serialPortName;
      }

   @Override
   public void addCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener)
      {
      if (listener != null)
         {
         createLabDevicePingFailureEventListeners.add(listener);
         }
      }

   @Override
   public void removeCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener)
      {
      if (listener != null)
         {
         createLabDevicePingFailureEventListeners.remove(listener);
         }
      }

   /** Returns <code>true</code> if the BrainLink is connected, <code>false</code> otherwise. */
   @Override
   public boolean isConnected()
      {
      return isConnected.get();
      }

   /**
    * Returns the current battery voltage in millivolts; returns <code>null</code> if the voltage could not be read.
    *
    * @return The battery voltage reading
    */
   @Override
   public Integer getBatteryVoltage()
      {
      // Converts from 8 bit ADC to battery voltage in millivolts
      final Integer rawValue = integerReturnValueCommandExecutor.execute(getBatteryVoltageCommandStrategy);
      if (rawValue != null)
         {
         return (rawValue * 2650) / 128;
         }
      return null;
      }

   /**
    * Returns true if the battery reads low (less than 3500 millivolts), false otherwise.
    *
    * @return Low battery status
    */
   @Override
   public boolean isBatteryLow()
      {
      return getBatteryVoltage() < 3500;
      }

   /**
    * Sets the color of the LED in the Brainlink.  The LED can be any color that can be created by mixing red, green,
    * and blue; turning on all three colors in equal amounts results in white light.  Valid ranges for the red, green,
    * and blue elements are 0 to 255.
    *
    * @param  red sets the intensity of the red element of the LED
    * @param  green sets the intensity of the green element of the LED
    * @param  blue sets the intensity of the blue element of the LED
    * @return <code>true</code> if LED was successfully set, <code>false</code> otherwise
    */
   @Override
   public boolean setFullColorLED(final int red, final int green, final int blue)
      {
      return noReturnValueCommandExecutor.execute(new FullColorLEDCommandStrategy(red, green, blue));
      }

   /**
    * Sets the full color LED to the given {@link Color}.
    *
    * @param   color sets the color of the LED
    * @return <code>true</code> if LED was successfully set, <code>false</code> otherwise
    */
   @Override
   public boolean setFullColorLED(final Color color)
      {
      return setFullColorLED(color.getRed(), color.getGreen(), color.getBlue());
      }

   /**
    * Returns the light sensor values; returns <code>null</code> if the light sensors could not be read.
    *
    * @return an int containing the light sensor value.
    */
   @Override
   public Integer getLightSensor()
      {
      return integerReturnValueCommandExecutor.execute(getPhotoresistorCommandStrategy);
      }

   /**
    * Returns the accelerometer values in Gs; returns <code>null</code> if the accelerometer could not be read.
    *
    * @return an array containing the X, Y, and Z accelerometer readings in Gs
    */
   @Override
   public double[] getAccelerometerValuesInGs()
      {
      final int[] rawValues = getRawAccelerometerState();
      if (rawValues != null)
         {
         return AccelerometerUnitConverterFreescaleMMA7660FC.getInstance().convert(rawValues);
         }
      return null;
      }

   /**
    * Returns the value of the accelerometer's X axis in Gs; returns <code>null</code> if the accelerometer could not be
    * read.
    *
    * @return the X acceleration in Gs
    */
   @Override
   public Double getXAccelerometer()
      {
      return getAccelerometerAxisValue(0);
      }

   /**
    * Returns the value of the accelerometer's Y axis in Gs; returns <code>null</code> if the accelerometer could not be
    * read.
    *
    * @return the Y Acceleration in Gs
    */
   @Override
   public Double getYAccelerometer()
      {
      return getAccelerometerAxisValue(1);
      }

   /**
    * Returns the value of the accelerometer's Z axis in Gs; returns <code>null</code> if the accelerometer could not be
    * read.
    *
    * @return the Z acceleration in Gs
    */
   @Override
   public Double getZAccelerometer()
      {
      return getAccelerometerAxisValue(2);
      }

   private Double getAccelerometerAxisValue(final int axisIndex)
      {
      if (0 <= axisIndex && axisIndex < BrainLinkConstants.ACCELEROMETER_AXIS_COUNT)
         {
         final double[] values = getAccelerometerValuesInGs();
         if (values != null)
            {
            return values[axisIndex];
            }
         }
      return null;
      }

   /**
    * Returns <code>true</code> if the BrainLink has been shaken since the last accelerometer read, <code>false</code>
    * otherise.  Returns <code>null</code> if the accelerometer could not be read.
    *
    * @return <code>true</code> if the accelerometer was shaken, <code>false</code> otherwise, <code>null</code> if read was unsuccessful
    */
   @Override
   public Boolean wasShaken()
      {
      final int[] rawValues = getRawAccelerometerState();
      if (rawValues != null)
         {
         final int tapShake = rawValues[3];
         return (tapShake & 128) == 128;     // apply mask to get the shaken state
         }
      return false;
      }

   /**
    * Returns <code>true</code> if the BrainLink has been tapped since the last accelerometer read, <code>false</code>
    * otherise.  Returns <code>null</code> if the accelerometer could not be read.
    *
    * @return <code>true</code> if the accelerometer was tapped, <code>false</code> otherwise, <code>null</code> if read was unsuccessful
    */
   @Override
   public Boolean wasTapped()
      {
      final int[] rawValues = getRawAccelerometerState();
      if (rawValues != null)
         {
         final int tapShake = rawValues[3];
         return (tapShake & 32) == 32;     // apply mask to get the tap state
         }
      return false;
      }

   /**
    * Returns <code>true</code> if the BrainLink has been shaken or tapped since the last accelerometer read,
    * <code>false</code> otherise.  Returns <code>null</code> if the accelerometer could not be read.
    *
    * @return <code>true</code> if the accelerometer was shaken or tapped, <code>false</code> otherwise, <code>null</code> if read was unsuccessful
    */
   @Override
   public Boolean wasShakenOTapped()
      {
      final int[] rawValues = getRawAccelerometerState();
      if (rawValues != null)
         {
         final int tapShake = rawValues[3];
         final boolean wasTapped = (tapShake & 32) == 32;
         final boolean wasShaken = (tapShake & 128) == 128;
         return wasTapped || wasShaken;
         }
      return false;
      }

   private int[] getRawAccelerometerState()
      {
      return intArrayReturnValueCommandExecutor.execute(getAccelerometerCommandStrategy);
      }

   /**
    * Returns the analog input values; returns <code>null</code> if the inputs could not be read.
    *
    * @return A six element array containing the raw sensor values of the six external analog inputs
    */
   @Override
   public int[] getAnalogInputs()
      {
      return intArrayReturnValueCommandExecutor.execute(getAnalogInputsCommandStrategy);
      }

   /**
    * Returns the value of the given analog input; returns <code>null</code> if the specified port could not be read or
    * is invalid.
    *
    * @return The raw analog reading (0-255) of one of the six external analog ports, <code>null</code> if the port couldn't be read
    */
   @Override
   public Integer getAnalogInput(final int port)
      {
      if (0 <= port && port < BrainLinkConstants.ANALOG_INPUT_COUNT)
         {
         final int[] inputs = getAnalogInputs();
         if (inputs != null)
            {
            return inputs[port];
            }
         }
      return null;
      }

   /**
    *  Returns true if the digital input is logic high, false if low, and null if the port was invalid or could not be read.
    *
    * @param port sets the input port to read, valid numbers are 0-9
    * @return <code>true</code> if the value on the port is logic high, <code>false</code> if logic low, and null if it
    * could not be read
    */
   @Override
   public Boolean getDigitalInput(final int port)
      {
      return booleanReturnValueCommandExecutor.execute(new DigitalInputCommandStrategy(port));
      }

   /**
    *  Sets one of the digital output ports; true for logic high, false for low.
    *
    * @param port sets the output port to use, valid numbers are 0-9
    * @param value sets the port to either logic high or low
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   @Override
   public boolean setDigitalOutput(final int port, final boolean value)
      {
      return noReturnValueCommandExecutor.execute(new DigitalOutputCommandStrategy(port, value));
      }

   /**
    * Sets the duty cycle of one of the two PWM ports.
    *
    * @param port the PWM port to set
    * @param dutyCycle the duty of the PWM signal, specified in % (0-100)
    * @param PWMfrequency the frequency of the PWM signal in Hertz.
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   @Override
   public boolean setPWM(final int port, final int dutyCycle, final int PWMfrequency)
      {
      return noReturnValueCommandExecutor.execute(new PWMCommandStrategy(port, dutyCycle, PWMfrequency));
      }

   /**
    * Sets the voltage of one of the two DAC ports
    *
    * @param port the DAC port to set
    * @param value the value, in milliVolts, to set the DAC to.
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   @Override
   public boolean setDAC(final int port, final int value)
      {
      return noReturnValueCommandExecutor.execute(new DACCommandStrategy(port, value));
      }

   /**
    *  Sets the baud rate of the auxiliary serial port. Only the baud rate is configurable. The serial port always uses
    *  8 bits, no flow control, and one stop bit.
    *
    * @param baudRate the baudrate, in baud, to configure the serial port to.
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   @Override
   public boolean configureSerialPort(final int baudRate)
      {
      return noReturnValueCommandExecutor.execute(new SetAuxSerialConfigurationCommandStrategy(baudRate));
      }

   /**
    * Transmits a stream of bytes over the auxiliary serial port.
    *
    * @param  bytesToSend an array of bytes to send over the serial port
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   @Override
   public boolean transmitBytesOverSerial(final byte[] bytesToSend)
      {
      return noReturnValueCommandExecutor.execute(new AuxSerialTransmitCommandStrategy(bytesToSend));
      }

   /**
    *  Returns the auxiliary serial port's receive buffer.
    *
    * @return an array of ints corresponding to the serial receive buffer. The buffer can only handle 256 bytes at a time,
    * so in high-data transfer applications this must be checked frequently.
    */
   @Override
   public int[] receiveBytesOverSerial()
      {
      return intArrayReturnValueCommandExecutor.execute(new AuxSerialReceiveCommandStrategy());
      }

   /**
    * Plays a tone specified at the frequency in hertz specified by frequency.  Tone will not stop until turnOffSpeaker
    * is called.
    *
    * @param frequency frequency in Hz of the tone to play
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   @Override
   public boolean playTone(final int frequency)
      {
      return noReturnValueCommandExecutor.execute(new PlayToneCommandStrategy(frequency));
      }

   /**
    * Turns off the speaker
    *
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   @Override
   public boolean turnOffSpeaker()
      {
      return noReturnValueCommandExecutor.execute(turnOffSpeakerCommandStrategy);
      }

   /**
    * Opens the device file specified by fileName and initializes the IR if the file is encoded. A number of encoded
    * files for popular robot platforms are provided in the "devices" directory, and you can make your own with the "StoreAndPlayEncodedSignals" utility.
    * The filename argument should not include the ".encsig" or ".rawsig" file name extensions - this is automatically appended.
    *
    * @param fileName The name of the file with Initialization data.
    * @param encoded If the file is encoded or raw
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   @Override
   @SuppressWarnings({"UseOfSystemOutOrSystemErr"})
   public boolean initializeDevice(final String fileName, final boolean encoded)
      {
      deviceFile = new BrainLinkFileManipulator(fileName, encoded);

      if (deviceFile.isEmpty())
         {
         System.out.println("Error, this file does not exist or is empty");
         return false;
         }

      if (encoded)
         {
         final byte[] initData = deviceFile.getInitialization();
         if (initData == null)
            {
            System.out.println("Error: No initialization in this encoded file");
            return false;
            }
         else
            {
            initializeIR(initData);
            return true;
            }
         }
      return true;
      }

   /**
    * Initializes the Infrared signal to mimic a given robot's communication protocol specified by initializationBytes.
    *
    * @param initializationBytes The bytes to send the Brainlink to configure it with a specific IR communication protocol.
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   @Override
   public boolean initializeIR(final byte[] initializationBytes)
      {
      return noReturnValueCommandExecutor.execute(new InitializeIRCommandStrategy(initializationBytes));
      }

   @Override
   public boolean sendSimpleIRCommand(final SimpleIRCommandStrategy commandStrategy)
      {
      return noReturnValueCommandExecutor.execute(commandStrategy);
      }

   @Override
   public boolean sendSimpleIRCommand(final byte command)
      {
      return sendSimpleIRCommand(new SimpleIRCommandStrategy(command));
      }

   /**
    * Turns off the IR signal, used in Stop methods in certain robot classes
    *
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   @Override
   public boolean turnOffIR()
      {
      return noReturnValueCommandExecutor.execute(turnOffIRCommandStrategy);
      }

   /**
    *  Sends the signal stored in fileName to Brainlink for transmission over IR. Handles both encoded and raw signal
    *  files.
    *
    * @param signalName The name of the signal
    * @return true if transmission succeeded
    * @throws IllegalStateException if the device has not yet been initialized with a call to {@link #initializeDevice(String, boolean)}
    */
   // Maybe split this into two functions so as to take advantage of the new functions for getting data
   // With the BrainLinkFileManipulator
   @Override
   @SuppressWarnings({"UseOfSystemOutOrSystemErr"})
   public boolean transmitIRSignal(final String signalName)
      {
      // do a null check for deviceFile, which can happen if the user didn't call initializeDevice first
      if (deviceFile == null)
         {
         throw new IllegalStateException("Cannot transmit IR signal because the device has not been initialized yet.  You must call initializeDevice() before calling this method.");
         }
      else
         {
         if (!deviceFile.containsSignal(signalName))
            {
            System.out.println("Error: signal not contained in file");
            return false;
            }

         final int[] signalValues = deviceFile.getSignalValues(signalName);
         final int repeatTime = deviceFile.getSignalRepeatTime(signalName);

         if (deviceFile.isEncoded())
            {
            final byte[] signalInBytes = new byte[signalValues.length];
            for (int i = 0; i < signalValues.length; i++)
               {
               signalInBytes[i] = (byte)signalValues[i];
               }
            final byte repeat1 = getHighByteFromInt(repeatTime);
            final byte repeat2 = getLowByteFromInt(repeatTime);
            return sendIRCommand(new IRCommandStrategy(signalInBytes, repeat1, repeat2));
            }
         else
            {
            return sendRawIR(signalValues, repeatTime);
            }
         }
      }

   /**
    * Used by transmitIRSignal if encoded is true. Sends an encoded IR command.
    *
    * @param commandStrategy An array of bytes encoding the command to send
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   @Override
   public boolean sendIRCommand(final IRCommandStrategy commandStrategy)
      {
      return noReturnValueCommandExecutor.execute(commandStrategy);
      }

   /**
    *  Will record any IR signal detected by the IR receiver, and returns this signal's measurements as an array of ints.
    *  Array elements are measurements in milliseconds of the time between the signal's falling or rising edges.
    *  The signal always begins with a rising edge and ends with a falling edge, therefore an even number of elements is always
    *  expected.
    *
    * @return An array of time measurements corresponding to an infrared signal.
    */
   @Override
   public int[] recordIR()
      {
      return intArrayReturnValueCommandExecutor.execute(new RecordIRCommandStrategy());
      }

   /**
    *  Stores the most recently recorded IR signal to the Brainlink's on-board EEPROM (which survives power cycling).
    *  There are five EEPROM positions to store IR signals to, so Brainlink can store up to 5 raw IR signals.
    *
    * @param position the position to store the IR signal to (range is 0 to 4)
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   @Override
   public boolean storeIR(final int position)
      {
      return noReturnValueCommandExecutor.execute(new StoreIRCommandStrategy(position));
      }

   /**
    *  Sends the IR signal recorded in the EEPROM position specified.
    *
    * @param position the position to play the IR signal from (range is 0 to 4)
    * @param repeatTime the amount of delay, in milliseconds, between successive signals. Use 0 if the signal should not repeat.
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   @Override
   public boolean playIR(final int position, final int repeatTime)
      {
      return noReturnValueCommandExecutor.execute(new PlayStoredIRCommandStrategy(position, repeatTime));
      }

   /**
    *  Used by transmitIRSignal. Sends a "Raw" format IR signal to transmit over the tether's IR LED.
    *
    * @param signal the raw IR signal consisting of time measurements.
    * @param repeatTime the amount of delay, in milliseconds, between successive signals. Use 0 if the signal should not repeat.
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   @Override
   public boolean sendRawIR(final int[] signal, final int repeatTime)
      {
      return noReturnValueCommandExecutor.execute(new SendRawIRCommandStrategy(signal, repeatTime));
      }

   /**
    *  Returns the IR signal recorded in the EEPROM position specified so that the host computer can read and analyze it.
    *  Note that the signal returned is of the same format as that returned by recordIR.
    *
    * @param position the position to print the IR signal from (range is 0 to 4)
    * @return An array of time measurements corresponding to an infrared signal, null if invalid.
    */
   @Override
   public int[] printIR(final int position)
      {
      return intArrayReturnValueCommandExecutor.execute(new PrintStoredIRCommandStrategy(position));
      }

   private byte getHighByteFromInt(final int val)
      {
      return (byte)((val << 16) >> 24);
      }

   private byte getLowByteFromInt(final int val)
      {
      return (byte)((val << 24) >> 24);
      }

   @SuppressWarnings({"UseOfSystemOutOrSystemErr"})
   public void sleep(final int millis)
      {
      try
         {
         if (millis > 0)
            {
            Thread.sleep(millis);
            }
         else
            {
            System.out.println("Error: sent negative time to sleep");
            }
         }
      catch (InterruptedException e)
         {
         System.out.println("Error while sleeping: " + e);
         }
      }

   public void disconnect()
      {
      disconnect(true);
      }

   private void disconnect(final boolean willAddDisconnectCommandToQueue)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("BrainLink.disconnect(" + willAddDisconnectCommandToQueue + ")");
         }

      // set the isConnected flag to false
      isConnected.set(false);

      // turn off the peer pinger
      try
         {
         peerPingScheduledFuture.cancel(false);
         peerPingScheduler.shutdownNow();
         LOG.debug("BrainLink.disconnect(): Successfully shut down BrainLink pinger.");
         }
      catch (Exception e)
         {
         LOG.error("BrainLink.disconnect(): Exception caught while trying to shut down peer pinger", e);
         }

      // optionally send goodbye command to the BrainLink
      if (willAddDisconnectCommandToQueue)
         {
         LOG.debug("BrainLink.disconnect(): Now attempting to send the disconnect command to the BrainLink");
         try
            {
            if (commandQueue.executeAndReturnStatus(disconnectCommandStrategy))
               {
               LOG.debug("BrainLink.disconnect(): Successfully disconnected from the BrainLink.");
               }
            else
               {
               LOG.error("BrainLink.disconnect(): Failed to disconnect from the BrainLink.");
               }
            }
         catch (Exception e)
            {
            LOG.error("Exception caught while trying to execute the disconnect", e);
            }
         }

      // shut down the command queue, which closes the serial port
      try
         {
         LOG.debug("BrainLink.disconnect(): shutting down the SerialDeviceCommandExecutionQueue...");
         commandQueue.shutdown();
         LOG.debug("BrainLink.disconnect(): done shutting down the SerialDeviceCommandExecutionQueue");
         }
      catch (Exception e)
         {
         LOG.error("BrainLink.disconnect(): Exception while trying to shut down the SerialDeviceCommandExecutionQueue", e);
         }
      }

   private class BrainLinkPinger implements Runnable
      {
      public void run()
         {
         try
            {
            // for pings, we simply get the battery voltage
            final boolean pingSuccessful = (getBatteryVoltage() != null);

            // if the ping failed, then we know we have a problem so disconnect (which
            // probably won't work) and then notify the listeners
            if (!pingSuccessful)
               {
               handlePingFailure();
               }
            }
         catch (Exception e)
            {
            LOG.error("BrainLink$BrainLinkPinger.run(): Exception caught while executing the peer pinger", e);
            }
         }

      private void handlePingFailure()
         {
         try
            {
            LOG.debug("BrainLink$BrainLinkPinger.handlePingFailure(): Peer ping failed.  Attempting to disconnect...");
            disconnect(false);
            LOG.debug("BrainLink$BrainLinkPinger.handlePingFailure(): Done disconnecting from the BrainLink");
            }
         catch (Exception e)
            {
            LOG.error("BrainLink$BrainLinkPinger.handlePingFailure(): Exeption caught while trying to disconnect from the BrainLink", e);
            }

         if (LOG.isDebugEnabled())
            {
            LOG.debug("BrainLink$BrainLinkPinger.handlePingFailure(): Notifying " + createLabDevicePingFailureEventListeners.size() + " listeners of ping failure...");
            }
         for (final CreateLabDevicePingFailureEventListener listener : createLabDevicePingFailureEventListeners)
            {
            try
               {
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("   BrainLink$BrainLinkPinger.handlePingFailure(): Notifying " + listener);
                  }
               listener.handlePingFailureEvent();
               }
            catch (Exception e)
               {
               LOG.error("BrainLink$BrainLinkPinger.handlePingFailure(): Exeption caught while notifying SerialDevicePingFailureEventListener", e);
               }
            }
         }

      private void forceFailure()
         {
         handlePingFailure();
         }
      }

   private static final class PseudoProxy implements CreateLabDeviceProxy
      {
      private final SerialDeviceCommandExecutionQueue commandExecutionQueue;
      private final String serialPortName;

      private PseudoProxy(final SerialDeviceCommandExecutionQueue commandExecutionQueue, final String serialPortName)
         {
         this.commandExecutionQueue = commandExecutionQueue;
         this.serialPortName = serialPortName;
         }

      public SerialDeviceCommandExecutionQueue getCommandExecutionQueue()
         {
         return commandExecutionQueue;
         }

      public String getSerialPortName()
         {
         return serialPortName;
         }

      @Override
      public String getPortName()
         {
         return getSerialPortName();
         }

      @Override
      public void disconnect()
         {
         // do nothing
         }

      @Override
      public void addCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener)
         {
         // do nothing
         }

      @Override
      public void removeCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener)
         {
         // do nothing
         }
      }

   private static final class BrainLinkConnectivityManager extends BaseCreateLabDeviceConnectivityManager<PseudoProxy>
      {
      @Override
      protected PseudoProxy scanForDeviceAndCreateProxy()
         {
         LOG.debug("BrainLink$BrainLinkConnectivityManager.scanForDeviceAndCreateProxy()");

         // If the user specified one or more serial ports, then just start trying to connect to it/them.  Otherwise,
         // check each available serial port for the target serial device, and connect to the first one found.  This
         // makes connection time much faster for when you know the name of the serial port.
         final SortedSet<String> availableSerialPorts;
         if (SerialPortEnumerator.didUserDefineSetOfSerialPorts())
            {
            availableSerialPorts = SerialPortEnumerator.getSerialPorts();
            }
         else
            {
            availableSerialPorts = SerialPortEnumerator.getAvailableSerialPorts();
            }

         // try the serial ports
         if ((availableSerialPorts != null) && (!availableSerialPorts.isEmpty()))
            {
            for (final String portName : availableSerialPorts)
               {
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("BrainLink$BrainLinkConnectivityManager.scanForDeviceAndCreateProxy(): checking serial port [" + portName + "]");
                  }

               final SerialDeviceCommandExecutionQueue queue = connect(portName);

               if (queue == null)
                  {
                  LOG.debug("BrainLink$BrainLinkConnectivityManager.scanForDeviceAndCreateProxy(): connection failed, maybe it's not the device we're looking for?");
                  }
               else
                  {
                  LOG.debug("BrainLink$BrainLinkConnectivityManager.scanForDeviceAndCreateProxy(): connection established!");
                  return new PseudoProxy(queue, portName);
                  }
               }
            }
         else
            {
            LOG.debug("BrainLink$BrainLinkConnectivityManager.scanForDeviceAndCreateProxy(): No available serial ports, returning null.");
            }

         return null;
         }

      private SerialDeviceCommandExecutionQueue connect(final String serialPortName)
         {
         // a little error checking...
         if (serialPortName == null)
            {
            throw new IllegalArgumentException("The serial port name may not be null");
            }

         // create the serial port configuration
         final SerialIOConfiguration config = new SerialIOConfiguration(serialPortName,
                                                                        BaudRate.BAUD_115200,
                                                                        CharacterSize.EIGHT,
                                                                        Parity.NONE,
                                                                        StopBits.ONE,
                                                                        FlowControl.NONE);

         try
            {
            // create the serial port command queue
            final SerialDeviceCommandExecutionQueue queue = SerialDeviceCommandExecutionQueue.create(APPLICATION_NAME, config, 0, null);

            // see whether its creation was successful
            if (queue == null)
               {
               if (LOG.isEnabledFor(Level.ERROR))
                  {
                  LOG.error("BrainLink$BrainLinkConnectivityManager.connect(): Failed to open serial port '" + serialPortName + "'");
                  }
               }
            else
               {
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("BrainLink$BrainLinkConnectivityManager.connect(): Serial port '" + serialPortName + "' opened.");
                  }

               // now try to do the handshake with the BrainLink to establish communication
               final boolean wasHandshakeSuccessful = queue.executeAndReturnStatus(new HandshakeCommandStrategy());

               // see if the handshake was a success
               if (wasHandshakeSuccessful)
                  {
                  LOG.info("BrainLink$BrainLinkConnectivityManager.connect(): BrainLink handshake successful!");

                  // now return the queue
                  return queue;
                  }
               else
                  {
                  LOG.error("BrainLink$BrainLinkConnectivityManager.connect(): Failed to handshake with BrainLink");
                  }

               // the handshake failed, so shutdown the command queue to release the serial port
               queue.shutdown();
               }
            }
         catch (Exception e)
            {
            LOG.error("BrainLink$BrainLinkConnectivityManager.connect(): Exception while trying to create the BrainLink", e);
            }

         return null;
         }
      }
   }
