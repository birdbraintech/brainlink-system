package edu.cmu.ri.createlab.brainlink.robots;

/**
 * <p>
 * <code>BrainLinkSolo</code> is a simple implementation of {@link BaseBrainLinkControllable} for when you want to write
 * applications with use the BrainLink by itself, and not connected with any other robot/device.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class BrainLinkSolo extends BaseBrainLinkControllable
   {
   public BrainLinkSolo()
      {
      this(null);
      }

   public BrainLinkSolo(final String userDefinedSerialPortNames)
      {
      super(userDefinedSerialPortNames);
      }

   @Override
   protected void prepareForDisconnect()
      {
      // nothing to do
      }
   }
