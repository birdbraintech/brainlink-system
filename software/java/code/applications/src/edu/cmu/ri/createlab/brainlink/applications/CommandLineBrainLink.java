package edu.cmu.ri.createlab.brainlink.applications;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import edu.cmu.ri.createlab.brainlink.BaseCommandLineBrainLink;
import edu.cmu.ri.createlab.brainlink.BrainLink;
import edu.cmu.ri.createlab.brainlink.BrainLinkProxy;
import edu.cmu.ri.createlab.device.CreateLabDevicePingFailureEventListener;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class CommandLineBrainLink extends BaseCommandLineBrainLink
   {
   public static void main(final String[] args)
      {
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      new CommandLineBrainLink(in).run();
      }

   private BrainLink brainLinkProxy;

   private CommandLineBrainLink(final BufferedReader in)
      {
      super(in);
      }

   protected boolean connect(final String serialPortName)
      {
      brainLinkProxy = BrainLinkProxy.create(serialPortName);

      if (brainLinkProxy == null)
         {
         println("Connection failed.");
         return false;
         }
      else
         {
         println("Connection successful.");
         brainLinkProxy.addCreateLabDevicePingFailureEventListener(
               new CreateLabDevicePingFailureEventListener()
               {
               public void handlePingFailureEvent()
                  {
                  println("BrainLink ping failure detected.  You will need to reconnect.");
                  brainLinkProxy = null;
                  }
               });
         return true;
         }
      }

   protected Integer getBatteryVoltage()
      {
      return brainLinkProxy.getBatteryVoltage();
      }

   protected void setFullColorLED(final int r, final int g, final int b)
      {
      brainLinkProxy.setFullColorLED(r, g, b);
      }

   protected void disconnect()
      {
      if (brainLinkProxy != null)
         {
         brainLinkProxy.disconnect();
         brainLinkProxy = null;
         }
      }

   protected boolean isInitialized()
      {
      return brainLinkProxy != null;
      }
   }
