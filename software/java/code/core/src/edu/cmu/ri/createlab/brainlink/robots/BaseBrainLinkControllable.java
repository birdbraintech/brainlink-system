package edu.cmu.ri.createlab.brainlink.robots;

import edu.cmu.ri.createlab.brainlink.BrainLink;
import edu.cmu.ri.createlab.device.CreateLabDevicePingFailureEventListener;
import edu.cmu.ri.createlab.device.connectivity.ConnectionException;
import edu.cmu.ri.createlab.serial.SerialPortEnumerator;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public abstract class BaseBrainLinkControllable implements BrainLinkControllable
   {
   private static final Logger LOG = Logger.getLogger(BaseBrainLinkControllable.class);

   private BrainLink brainLink = null;

   /**
    * Creates the <code>BaseBrainLinkControllable</code> by attempting to connect to all available serial ports and
    * connecting to the first BrainLink it finds.
    */
   public BaseBrainLinkControllable()
      {
      this(null);
      }

   /**
    * Creates the <code>BaseBrainLinkControllable</code> by attempting to connect to a BrainLink on the given serial
    * port(s).  Note that if one ore more serial ports is already specified as a system property (e.g. by using the -D
    * command line switch), then the serial port(s) specified in the argument to this constructor are appended to the
    * port names specified in the system property, and the system property value is updated.
    */
   public BaseBrainLinkControllable(final String userDefinedSerialPortNames)
      {
      if (userDefinedSerialPortNames != null && userDefinedSerialPortNames.trim().length() > 0)
         {
         LOG.debug("BaseBrainLinkControllable.BaseBrainLinkControllable(): processing user-defined serial port names...");

         // initialize the set of names to those specified in the argument to this constructor
         final StringBuilder serialPortNames = new StringBuilder(userDefinedSerialPortNames);

         // Now see if there are also serial ports already specified in the system property (e.g. via the -D command line switch).
         // If so, then those take precedence and the ones specified in the constructor argument will be appended
         final String serialPortNamesAlreadyInSystemProperty = System.getProperty(SerialPortEnumerator.SERIAL_PORTS_SYSTEM_PROPERTY_KEY, null);
         if (serialPortNamesAlreadyInSystemProperty != null && serialPortNamesAlreadyInSystemProperty.trim().length() > 0)
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("BaseBrainLinkControllable.BaseBrainLinkControllable(): Existing system property value = [" + serialPortNamesAlreadyInSystemProperty + "]");
               }
            serialPortNames.insert(0, System.getProperty("path.separator", ":"));
            serialPortNames.insert(0, serialPortNamesAlreadyInSystemProperty);
            }

         // now set the system property
         System.setProperty(SerialPortEnumerator.SERIAL_PORTS_SYSTEM_PROPERTY_KEY, serialPortNames.toString());
         if (LOG.isDebugEnabled())
            {
            LOG.debug("BaseBrainLinkControllable.BaseBrainLinkControllable(): System property [" + SerialPortEnumerator.SERIAL_PORTS_SYSTEM_PROPERTY_KEY + "] now set to [" + serialPortNames + "]");
            }
         }

      System.out.println("Connecting to BrainLink...this may take a few seconds...");

      try
         {
         LOG.debug("Connecting to the BrainLink...");
         brainLink = new BrainLinkConnectivityManager().connect();
         brainLink.addCreateLabDevicePingFailureEventListener(
               new CreateLabDevicePingFailureEventListener()
               {
               @Override
               public void handlePingFailureEvent()
                  {
                  LOG.debug("BaseBrainLinkControllable.handlePingFailureEvent()");
                  brainLink = null;
                  }
               }
         );
         }
      catch (ConnectionException e)
         {
         LOG.error("ConnectionException while trying to connect to the BrainLink", e);
         }
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
