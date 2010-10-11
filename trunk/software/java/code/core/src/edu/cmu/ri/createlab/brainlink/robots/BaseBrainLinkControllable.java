package edu.cmu.ri.createlab.brainlink.robots;

import java.util.concurrent.Semaphore;
import edu.cmu.ri.createlab.brainlink.BrainLink;
import edu.cmu.ri.createlab.brainlink.BrainLinkProxy;
import edu.cmu.ri.createlab.device.connectivity.CreateLabDeviceConnectionEventListener;
import edu.cmu.ri.createlab.device.connectivity.CreateLabDeviceConnectionState;
import edu.cmu.ri.createlab.device.connectivity.CreateLabDeviceConnectivityManager;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public abstract class BaseBrainLinkControllable implements BrainLinkControllable
   {
   private static final Logger LOG = Logger.getLogger(BaseBrainLinkControllable.class);

   private final Semaphore connectionCompleteSemaphore = new Semaphore(1);
   private final CreateLabDeviceConnectivityManager connectivityManager = new BrainLinkConnectivityManager();

   private BrainLink brainLink = null;

   public BaseBrainLinkControllable()
      {
      System.out.println("Connecting to BrainLink...this may take a few seconds...");

      connectivityManager.addConnectionEventListener(
            new CreateLabDeviceConnectionEventListener()
            {
            public void handleConnectionStateChange(final CreateLabDeviceConnectionState oldState, final CreateLabDeviceConnectionState newState, final String portName)
               {
               if (CreateLabDeviceConnectionState.CONNECTED.equals(newState))
                  {
                  LOG.debug("BaseBrainLinkControllable.handleConnectionStateChange(): Connected");

                  // connection complete, so release the lock
                  connectionCompleteSemaphore.release();
                  brainLink = (BrainLinkProxy)connectivityManager.getCreateLabDeviceProxy();
                  }
               else if (CreateLabDeviceConnectionState.DISCONNECTED.equals(newState))
                  {
                  LOG.debug("BaseBrainLinkControllable.handleConnectionStateChange(): Disconnected");
                  brainLink = null;
                  }
               else if (CreateLabDeviceConnectionState.SCANNING.equals(newState))
                  {
                  LOG.debug("BaseBrainLinkControllable.handleConnectionStateChange(): Scanning...");
                  }
               else
                  {
                  LOG.error("BaseBrainLinkControllable.handleConnectionStateChange(): Unexpected CreateLabDeviceConnectionState [" + newState + "]");
                  brainLink = null;
                  }
               }
            });

      LOG.trace("BrainLink.BrainLink(): 1) aquiring connection lock");

      // acquire the lock, which will be released once the connection is complete
      connectionCompleteSemaphore.acquireUninterruptibly();

      LOG.trace("BrainLink.BrainLink(): 2) connecting");

      // try to connect
      connectivityManager.scanAndConnect();

      LOG.trace("BrainLink.BrainLink(): 3) waiting for connection to complete");

      // try to acquire the lock again, which will block until the connection is complete
      connectionCompleteSemaphore.acquireUninterruptibly();

      LOG.trace("BrainLink.BrainLink(): 4) releasing lock");

      // we know the connection has completed (i.e. either connected or the connection failed) at this point, so just release the lock
      connectionCompleteSemaphore.release();

      LOG.trace("BrainLink.BrainLink(): 5) make sure we're actually connected");

      // if we're not connected, then throw an exception
      if (!CreateLabDeviceConnectionState.CONNECTED.equals(connectivityManager.getConnectionState()))
         {
         LOG.error("BrainLink.BrainLink(): Failed to connect to the BrainLink!  Aborting.");
         System.exit(1);
         }

      LOG.trace("BrainLink.BrainLink(): 6) All done!");
      }

   public final BrainLink getBrainLink()
      {
      return brainLink;
      }

   public final void disconnect()
      {
      if (brainLink != null)
         {
         prepareForDisconnect();
         brainLink.disconnect();
         }
      }

   protected abstract void prepareForDisconnect();
   }
