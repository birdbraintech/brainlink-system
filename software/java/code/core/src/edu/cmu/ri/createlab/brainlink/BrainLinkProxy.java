package edu.cmu.ri.createlab.brainlink;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import edu.cmu.ri.createlab.brainlink.commands.FullColorLEDCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.GetBatteryVoltageCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.ReturnValueCommandStrategy;
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
   private static final int DELAY_BETWEEN_PEER_PINGS = 2;

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

         // TODO: do handshake?
         return new BrainLinkProxy(commandQueue);
         }
      }
   catch (Exception e)
      {
      LOG.error("Exception while trying to create the BrainLinkProxy", e);
      }

   return null;
   }

   private final SerialPortCommandExecutionQueue commandQueue;
   private final ReturnValueCommandExecutor<Integer> getBatteryVoltageCommandExecutor = new ReturnValueCommandExecutor<Integer>(new GetBatteryVoltageCommandStrategy());

   private final BrainLinkPinger brainLinkPinger = new BrainLinkPinger();
   private final ScheduledExecutorService peerPingScheduler = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory("BrainLinkProxy.peerPingScheduler"));
   private final ScheduledFuture<?> peerPingScheduledFuture;
   private final Collection<CreateLabDevicePingFailureEventListener> createLabDevicePingFailureEventListeners = new HashSet<CreateLabDevicePingFailureEventListener>();

   private BrainLinkProxy(final SerialPortCommandExecutionQueue commandQueue)
      {
      this.commandQueue = commandQueue;

      // schedule periodic peer pings
      peerPingScheduledFuture = peerPingScheduler.scheduleAtFixedRate(brainLinkPinger,
                                                                      DELAY_BETWEEN_PEER_PINGS, // delay before first ping
                                                                      DELAY_BETWEEN_PEER_PINGS, // delay between pings
                                                                      TimeUnit.SECONDS);
      }

   public String getPortName()
      {
      return null;
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
      final NoReturnValueCommandExecutor commandExecutor = new NoReturnValueCommandExecutor(new FullColorLEDCommandStrategy(red, green, blue));
      return commandExecutor.executeAndReturnStatus();
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
      /*
      // TODO: implement this if Tom adds a disconnect command
      if (willAddDisconnectCommandToQueue)
         {
         LOG.debug("BrainLinkProxy.disconnect(): Now attempting to send the disconnect command to the BrainLink");
         if (commandQueue.executeAndReturnStatus(disconnectCommandStrategy))
            {
            LOG.debug("BrainLinkProxy.disconnect(): Successfully disconnected from the BrainLink.");
            }
         else
            {
            LOG.error("BrainLinkProxy.disconnect(): Failed to disconnect from the BrainLink.");
            }
         }
      */

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
      private final CreateLabSerialDeviceNoReturnValueCommandStrategy commandStrategy;

      private NoReturnValueCommandExecutor(final CreateLabSerialDeviceNoReturnValueCommandStrategy commandStrategy)
         {
         this.commandStrategy = commandStrategy;
         }

      private boolean executeAndReturnStatus()
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
