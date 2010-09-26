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

   private BrainLink brainLink;

   private CommandLineBrainLink(final BufferedReader in)
      {
      super(in);
      }

   protected boolean connect(final String serialPortName)
      {
      brainLink = BrainLinkProxy.create(serialPortName);

      if (brainLink == null)
         {
         println("Connection failed.");
         return false;
         }
      else
         {
         println("Connection successful.");
         brainLink.addCreateLabDevicePingFailureEventListener(
               new CreateLabDevicePingFailureEventListener()
               {
               public void handlePingFailureEvent()
                  {
                  println("BrainLink ping failure detected.  You will need to reconnect.");
                  brainLink = null;
                  }
               });
         return true;
         }
      }

   protected Integer getBatteryVoltage()
      {
      return brainLink.getBatteryVoltage();
      }

   protected void setFullColorLED(final int r, final int g, final int b)
      {
      brainLink.setFullColorLED(r, g, b);
      }

   protected void disconnect()
      {
      if (brainLink != null)
         {
         brainLink.disconnect();
         brainLink = null;
         }
      }

   protected boolean isInitialized()
      {
      return brainLink != null;
      }
   }
