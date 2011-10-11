package edu.cmu.ri.createlab.brainlink.applications;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import edu.cmu.ri.createlab.brainlink.BrainLink;
import edu.cmu.ri.createlab.brainlink.BrainLinkInterface;

/**
 * <p>
 * <code>BrainLinkBlinkyLED</code> blinks the BrainLink's LED.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public final class BrainLinkBlinkyLED
   {
   private static final int SLEEP_INCREMENT_MILLIS = 50;
   private static final Color[] COLORS = new Color[]{Color.RED, Color.GREEN, Color.BLUE};

   public static void main(final String[] args)
      {
      new BrainLinkBlinkyLED().run();
      }

   private void run()
      {
      final BrainLinkInterface brainLink = new BrainLink();

      System.out.println("");
      System.out.println("Press ENTER to quit.");

      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      int colorIndex = 0;

      do
         {
         brainLink.setFullColorLED(COLORS[colorIndex]);
         colorIndex = (colorIndex >= COLORS.length - 1) ? 0 : colorIndex + 1;
         }
      while (brainLink.isConnected() && !sleep(in, 100));

      brainLink.disconnect();
      }

   @SuppressWarnings({"BusyWait"})
   private static boolean sleep(final BufferedReader in, final long millis)
      {
      if (millis > 0)
         {
         long millisLeftToSleep = millis;
         while (millisLeftToSleep > 0)
            {
            final long sleepDurationMillis;
            if (millisLeftToSleep > SLEEP_INCREMENT_MILLIS)
               {
               sleepDurationMillis = SLEEP_INCREMENT_MILLIS;
               millisLeftToSleep -= SLEEP_INCREMENT_MILLIS;
               }
            else
               {
               sleepDurationMillis = millisLeftToSleep;
               millisLeftToSleep = 0;
               }

            // sleep
            try
               {
               Thread.sleep(sleepDurationMillis);
               }
            catch (InterruptedException e)
               {
               System.err.println("InterruptedException while sleeping: " + e);
               }

            // check whether the user hit ENTER while we were sleeping
            if (shouldQuit(in))
               {
               return true;
               }
            }
         }
      return false;
      }

   private static boolean shouldQuit(final BufferedReader in)
      {
      try
         {
         // check whether the user pressed ENTER
         if (in.ready())
            {
            return true;
            }
         }
      catch (IOException e)
         {
         System.err.println("IOException while reading user input: " + e);
         }
      return false;
      }
   }
