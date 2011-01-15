package edu.cmu.ri.createlab.brainlink;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import edu.cmu.ri.createlab.brainlink.commands.DisconnectCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.FullColorLEDCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.GetAccelerometerCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.GetAnalogInputsCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.GetBatteryVoltageCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.GetPhotoresistorCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.GetThermistorCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.HandshakeCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.InitializeIRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.PlayToneCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.ReturnValueCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.SimpleIRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.TurnOffSpeakerCommandStrategy;
import edu.cmu.ri.createlab.device.CreateLabDevicePingFailureEventListener;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialPortCommandExecutionQueue;
import edu.cmu.ri.createlab.serial.SerialPortCommandResponse;
import edu.cmu.ri.createlab.serial.config.BaudRate;
import edu.cmu.ri.createlab.serial.config.CharacterSize;
import edu.cmu.ri.createlab.serial.config.FlowControl;
import edu.cmu.ri.createlab.serial.config.Parity;
import edu.cmu.ri.createlab.serial.config.SerialIOConfiguration;
import edu.cmu.ri.createlab.serial.config.StopBits;
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
         final SerialPortCommandExecutionQueue commandQueue = SerialPortCommandExecutionQueue.create(APPLICATION_NAME, config);

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

   private final SerialPortCommandExecutionQueue commandQueue;
   private final String serialPortName;
   private final DisconnectCommandStrategy disconnectCommandStrategy = new DisconnectCommandStrategy();
   private final NoReturnValueCommandExecutor noReturnValueCommandExecutor = new NoReturnValueCommandExecutor();
   private final ReturnValueCommandExecutor<Integer> getBatteryVoltageCommandExecutor = new ReturnValueCommandExecutor<Integer>(new GetBatteryVoltageCommandStrategy());
   private final ReturnValueCommandExecutor<int[]> getAccelerometerStateCommandExecutor = new ReturnValueCommandExecutor<int[]>(new GetAccelerometerCommandStrategy());
   private final ReturnValueCommandExecutor<int[]> getPhotoresistorsCommandExecutor = new ReturnValueCommandExecutor<int[]>(new GetPhotoresistorCommandStrategy());
   private final ReturnValueCommandExecutor<int[]> getAnalogInputsCommandExecutor = new ReturnValueCommandExecutor<int[]>(new GetAnalogInputsCommandStrategy());
   private final ReturnValueCommandExecutor<Integer> getThermistorCommandExecutor = new ReturnValueCommandExecutor<Integer>(new GetThermistorCommandStrategy());

   private final BrainLinkPinger brainLinkPinger = new BrainLinkPinger();
   private final ScheduledExecutorService peerPingScheduler = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory("BrainLinkProxy.peerPingScheduler"));
   private final ScheduledFuture<?> peerPingScheduledFuture;
   private final Collection<CreateLabDevicePingFailureEventListener> createLabDevicePingFailureEventListeners = new HashSet<CreateLabDevicePingFailureEventListener>();

   private BrainLinkProxy(final SerialPortCommandExecutionQueue commandQueue, final String serialPortName)
      {
      this.commandQueue = commandQueue;
      this.serialPortName = serialPortName;

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
      return getBatteryVoltageCommandExecutor.execute();
      }

   public boolean setFullColorLED(final int red, final int green, final int blue)
      {
      return noReturnValueCommandExecutor.executeAndReturnStatus(new FullColorLEDCommandStrategy(red, green, blue));
      }

   public boolean setFullColorLED(final Color color)
      {
      return setFullColorLED(color.getRed(), color.getGreen(), color.getBlue());
      }

   public int[] getPhotoresistors()
      {
      return getPhotoresistorsCommandExecutor.execute();
      }

   public int[] getAccelerometerState()
      {
      return getAccelerometerStateCommandExecutor.execute();
      }

   public int[] getAnalogInputs()
      {
      return getAnalogInputsCommandExecutor.execute();
      }

   public Integer getThermistor()
      {
      return getThermistorCommandExecutor.execute();
      }

   public boolean playTone(final int frequency)
      {
      return noReturnValueCommandExecutor.executeAndReturnStatus(new PlayToneCommandStrategy(frequency));
      }

   public boolean turnOffSpeaker()
      {
      return noReturnValueCommandExecutor.executeAndReturnStatus(new TurnOffSpeakerCommandStrategy());
      }

   public boolean initializeIR(final byte[] initializationBytes)
      {
      return noReturnValueCommandExecutor.executeAndReturnStatus(new InitializeIRCommandStrategy(initializationBytes));
      }

   public boolean sendSimpleIRCommand(final SimpleIRCommandStrategy commandStrategy)
      {
      return noReturnValueCommandExecutor.executeAndReturnStatus(commandStrategy);
      }

   public boolean sendSimpleIRCommand(final byte command)
      {
      return sendSimpleIRCommand(new SimpleIRCommandStrategy(command));
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
         LOG.debug("BrainLinkProxy.disconnect(): shutting down the SerialPortCommandExecutionQueue...");
         commandQueue.shutdown();
         LOG.debug("BrainLinkProxy.disconnect(): done shutting down the SerialPortCommandExecutionQueue");
         }
      catch (Exception e)
         {
         LOG.error("BrainLinkProxy.disconnect(): Exception while trying to shut down the SerialPortCommandExecutionQueue", e);
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

   private final class ReturnValueCommandExecutor<T>
      {
      private final ReturnValueCommandStrategy<T> commandStrategy;

      private ReturnValueCommandExecutor(final ReturnValueCommandStrategy<T> commandStrategy)
         {
         this.commandStrategy = commandStrategy;
         }

      private T execute()
         {
         try
            {
            final SerialPortCommandResponse response = commandQueue.execute(commandStrategy);
            return commandStrategy.convertResult(response);
            }
         catch (Exception e)
            {
            LOG.error("Exception caught while trying to execute a command", e);
            brainLinkPinger.forceFailure();
            }

         return null;
         }
      }

   private final class NoReturnValueCommandExecutor
      {
      private boolean executeAndReturnStatus(final CreateLabSerialDeviceNoReturnValueCommandStrategy commandStrategy)
         {
         try
            {
            return commandQueue.executeAndReturnStatus(commandStrategy);
            }
         catch (Exception e)
            {
            LOG.error("Exception caught while trying to execute a command", e);
            brainLinkPinger.forceFailure();
            }

         return false;
         }
      }
   }
