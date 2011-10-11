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
import edu.cmu.ri.createlab.brainlink.commands.GetThermistorCommandStrategy;
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
   private final GetThermistorCommandStrategy getThermistorCommandStrategy = new GetThermistorCommandStrategy();
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

   public String getPortName()
      {
      return serialPortName;
      }

   public void addCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener)
      {
      if (listener != null)
         {
         createLabDevicePingFailureEventListeners.add(listener);
         }
      }

   public void removeCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener)
      {
      if (listener != null)
         {
         createLabDevicePingFailureEventListeners.remove(listener);
         }
      }

   @Override
   public boolean isConnected()
      {
      return isConnected.get();
      }

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

   public boolean isBatteryLow()
      {
      return getBatteryVoltage() < 3500;
      }

   public boolean setFullColorLED(final int red, final int green, final int blue)
      {
      return noReturnValueCommandExecutor.execute(new FullColorLEDCommandStrategy(red, green, blue));
      }

   public boolean setFullColorLED(final Color color)
      {
      return setFullColorLED(color.getRed(), color.getGreen(), color.getBlue());
      }

   public Integer getLightSensor()
      {
      return integerReturnValueCommandExecutor.execute(getPhotoresistorCommandStrategy);
      }

   public double[] getAccelerometerValuesInGs()
      {
      final int[] rawValues = getRawAccelerometerState();
      if (rawValues != null)
         {
         return AccelerometerUnitConverterFreescaleMMA7660FC.getInstance().convert(rawValues);
         }
      return null;
      }

   public Double getXAccelerometer()
      {
      return getAccelerometerAxisValue(0);
      }

   public Double getYAccelerometer()
      {
      return getAccelerometerAxisValue(1);
      }

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

   public int[] getAnalogInputs()
      {
      return intArrayReturnValueCommandExecutor.execute(getAnalogInputsCommandStrategy);
      }

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

   public Boolean getDigitalInput(final int port)
      {
      return booleanReturnValueCommandExecutor.execute(new DigitalInputCommandStrategy(port));
      }

   public boolean setDigitalOutput(final int port, final boolean value)
      {
      return noReturnValueCommandExecutor.execute(new DigitalOutputCommandStrategy(port, value));
      }

   public boolean setPWM(final int port, final int dutyCycle, final int PWMfrequency)
      {
      return noReturnValueCommandExecutor.execute(new PWMCommandStrategy(port, dutyCycle, PWMfrequency));
      }

   public boolean setDAC(final int port, final int value)
      {
      return noReturnValueCommandExecutor.execute(new DACCommandStrategy(port, value));
      }

   public boolean configureSerialPort(final int baudRate)
      {
      return noReturnValueCommandExecutor.execute(new SetAuxSerialConfigurationCommandStrategy(baudRate));
      }

   public boolean transmitBytesOverSerial(final byte[] bytesToSend)
      {
      return noReturnValueCommandExecutor.execute(new AuxSerialTransmitCommandStrategy(bytesToSend));
      }

   public int[] receiveBytesOverSerial()
      {
      return intArrayReturnValueCommandExecutor.execute(new AuxSerialReceiveCommandStrategy());
      }

   public boolean playTone(final int frequency)
      {
      return noReturnValueCommandExecutor.execute(new PlayToneCommandStrategy(frequency));
      }

   public boolean turnOffSpeaker()
      {
      return noReturnValueCommandExecutor.execute(turnOffSpeakerCommandStrategy);
      }

   public boolean initializeIR(final byte[] initializationBytes)
      {
      return noReturnValueCommandExecutor.execute(new InitializeIRCommandStrategy(initializationBytes));
      }

   public boolean sendSimpleIRCommand(final SimpleIRCommandStrategy commandStrategy)
      {
      return noReturnValueCommandExecutor.execute(commandStrategy);
      }

   public boolean sendSimpleIRCommand(final byte command)
      {
      return sendSimpleIRCommand(new SimpleIRCommandStrategy(command));
      }

   public boolean turnOffIR()
      {
      return noReturnValueCommandExecutor.execute(turnOffIRCommandStrategy);
      }

   public boolean sendIRCommand(final IRCommandStrategy commandStrategy)
      {
      return noReturnValueCommandExecutor.execute(commandStrategy);
      }

   public int[] recordIR()
      {
      return intArrayReturnValueCommandExecutor.execute(new RecordIRCommandStrategy());
      }

   public boolean storeIR(final int position)
      {
      return noReturnValueCommandExecutor.execute(new StoreIRCommandStrategy(position));
      }

   public boolean playIR(final int position, final int repeatTime)
      {
      return noReturnValueCommandExecutor.execute(new PlayStoredIRCommandStrategy(position, repeatTime));
      }

   public boolean sendRawIR(final int[] signal, final int repeatTime)
      {
      return noReturnValueCommandExecutor.execute(new SendRawIRCommandStrategy(signal, repeatTime));
      }

   public int[] printIR(final int position)
      {
      return intArrayReturnValueCommandExecutor.execute(new PrintStoredIRCommandStrategy(position));
      }

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

   // Maybe split this into two functions so as to take advantage of the new functions for getting data
   // With the BrainLinkFileManipulator
   @SuppressWarnings({"UseOfSystemOutOrSystemErr"})
   public boolean transmitIRSignal(final String signalName)
      {
      // TODO: add null check for deviceFile (which can happen if the user didn't call initializeDevice first
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
