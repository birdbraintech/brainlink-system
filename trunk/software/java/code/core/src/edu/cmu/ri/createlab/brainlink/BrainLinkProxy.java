package edu.cmu.ri.createlab.brainlink;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandExecutionQueue;
import edu.cmu.ri.createlab.serial.SerialDeviceNoReturnValueCommandExecutor;
import edu.cmu.ri.createlab.serial.SerialDeviceReturnValueCommandExecutor;
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
public final class BrainLinkProxy implements BrainLink
   {
   private static final Logger LOG = Logger.getLogger(BrainLinkProxy.class);

   public static final String APPLICATION_NAME = "BrainLinkProxy";
   private static final int DELAY_IN_SECONDS_BETWEEN_PEER_PINGS = 2;

   /**
    * Tries to create a <code>BrainLinkProxy</code> for the the serial port specified by the given
    * <code>serialPortName</code>. Returns <code>null</code> if the connection could not be established.
    *
    * @param serialPortName - the name of the serial port device which should be used to establish the connection
    *
    * @throws IllegalArgumentException if the <code>serialPortName</code> is <code>null</code>
    */
   public static BrainLinkProxy create(final String serialPortName)
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
         final SerialDeviceCommandExecutionQueue commandQueue = SerialDeviceCommandExecutionQueue.create(APPLICATION_NAME, config);

         // see whether its creation was successful
         if (commandQueue == null)
            {
            if (LOG.isEnabledFor(Level.ERROR))
               {
               LOG.error("Failed to open serial port '" + serialPortName + "'");
               }
            }
         else
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("Serial port '" + serialPortName + "' opened.");
               }

            // now try to do the handshake with the BrainLink to establish communication
            final boolean wasHandshakeSuccessful = commandQueue.executeAndReturnStatus(new HandshakeCommandStrategy());

            // see if the handshake was a success
            if (wasHandshakeSuccessful)
               {
               LOG.info("BrainLink handshake successful!");

               // now create and return the proxy
               return new BrainLinkProxy(commandQueue, serialPortName);
               }
            else
               {
               LOG.error("Failed to handshake with BrainLink");
               }

            // the handshake failed, so shutdown the command queue to release the serial port
            commandQueue.shutdown();
            }
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to create the BrainLinkProxy", e);
         }

      return null;
      }

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

   private final BrainLinkPinger brainLinkPinger = new BrainLinkPinger();
   private final ScheduledExecutorService peerPingScheduler = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory("BrainLinkProxy.peerPingScheduler"));
   private final ScheduledFuture<?> peerPingScheduledFuture;
   private final Collection<CreateLabDevicePingFailureEventListener> createLabDevicePingFailureEventListeners = new HashSet<CreateLabDevicePingFailureEventListener>();

   private BrainLinkProxy(final SerialDeviceCommandExecutionQueue commandQueue, final String serialPortName)
      {
      this.commandQueue = commandQueue;
      this.serialPortName = serialPortName;

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

   public Integer getBatteryVoltage()
      {
      return integerReturnValueCommandExecutor.execute(getBatteryVoltageCommandStrategy);
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

   public Integer getThermistor()
      {
      return integerReturnValueCommandExecutor.execute(getThermistorCommandStrategy);
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

   public void disconnect()
      {
      disconnect(true);
      }

   private void disconnect(final boolean willAddDisconnectCommandToQueue)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("BrainLinkProxy.disconnect(" + willAddDisconnectCommandToQueue + ")");
         }

      // turn off the peer pinger
      try
         {
         peerPingScheduledFuture.cancel(false);
         peerPingScheduler.shutdownNow();
         LOG.debug("BrainLinkProxy.disconnect(): Successfully shut down BrainLink pinger.");
         }
      catch (Exception e)
         {
         LOG.error("BrainLinkProxy.disconnect(): Exception caught while trying to shut down peer pinger", e);
         }

      // optionally send goodbye command to the BrainLink
      if (willAddDisconnectCommandToQueue)
         {
         LOG.debug("BrainLinkProxy.disconnect(): Now attempting to send the disconnect command to the BrainLink");
         try
            {
            if (commandQueue.executeAndReturnStatus(disconnectCommandStrategy))
               {
               LOG.debug("BrainLinkProxy.disconnect(): Successfully disconnected from the BrainLink.");
               }
            else
               {
               LOG.error("BrainLinkProxy.disconnect(): Failed to disconnect from the BrainLink.");
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
         LOG.debug("BrainLinkProxy.disconnect(): shutting down the SerialDeviceCommandExecutionQueue...");
         commandQueue.shutdown();
         LOG.debug("BrainLinkProxy.disconnect(): done shutting down the SerialDeviceCommandExecutionQueue");
         }
      catch (Exception e)
         {
         LOG.error("BrainLinkProxy.disconnect(): Exception while trying to shut down the SerialDeviceCommandExecutionQueue", e);
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
            LOG.error("BrainLinkProxy$BrainLinkPinger.run(): Exception caught while executing the peer pinger", e);
            }
         }

      private void handlePingFailure()
         {
         try
            {
            LOG.debug("BrainLinkProxy$BrainLinkPinger.handlePingFailure(): Peer ping failed.  Attempting to disconnect...");
            disconnect(false);
            LOG.debug("BrainLinkProxy$BrainLinkPinger.handlePingFailure(): Done disconnecting from the BrainLink");
            }
         catch (Exception e)
            {
            LOG.error("BrainLinkProxy$BrainLinkPinger.handlePingFailure(): Exeption caught while trying to disconnect from the BrainLink", e);
            }

         if (LOG.isDebugEnabled())
            {
            LOG.debug("BrainLinkProxy$BrainLinkPinger.handlePingFailure(): Notifying " + createLabDevicePingFailureEventListeners.size() + " listeners of ping failure...");
            }
         for (final CreateLabDevicePingFailureEventListener listener : createLabDevicePingFailureEventListeners)
            {
            try
               {
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("   BrainLinkProxy$BrainLinkPinger.handlePingFailure(): Notifying " + listener);
                  }
               listener.handlePingFailureEvent();
               }
            catch (Exception e)
               {
               LOG.error("BrainLinkProxy$BrainLinkPinger.handlePingFailure(): Exeption caught while notifying SerialDevicePingFailureEventListener", e);
               }
            }
         }

      private void forceFailure()
         {
         handlePingFailure();
         }
      }
   }
