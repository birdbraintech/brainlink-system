package edu.cmu.ri.createlab.brainlink.applications;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import edu.cmu.ri.createlab.brainlink.robots.robosapien.Robosapien;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public final class RobosapienDance
   {
   public static void main(final String[] args) throws IOException
      {
      final Robosapien robosapien = new Robosapien();

      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      System.out.println("");
      System.out.println("Press ENTER to quit.");

      while (true)
         {
         // check whether the user pressed a key
         if (in.ready())
            {
            break;
            }

         robosapien.getBrainLink().setFullColorLED(Color.GREEN);
         robosapien.walkForward();

         sleep(2000);

         robosapien.getBrainLink().setFullColorLED(Color.RED);
         robosapien.stop();

         sleep(1000);

         robosapien.getBrainLink().setFullColorLED(Color.BLUE);
         robosapien.walkBackward();

         sleep(2000);

         robosapien.getBrainLink().setFullColorLED(Color.RED);
         robosapien.stop();

         sleep(1000);

         robosapien.turnRight();

         sleep(2000);

         robosapien.turnLeft();

         sleep(2000);

         robosapien.turnRight();

         sleep(2000);

         robosapien.turnLeft();

         sleep(2000);

         robosapien.rightArmIn();
         robosapien.leftArmIn();

         sleep(2000);

         robosapien.rightArmOut();
         robosapien.leftArmOut();

         sleep(2000);

         robosapien.rightArmUp();
         robosapien.leftArmUp();

         sleep(2000);

         robosapien.rightArmDown();
         robosapien.leftArmDown();

         sleep(2000);
         }

      robosapien.disconnect();
      }

   private static void sleep(final int millis)
      {
      try
         {
         Thread.sleep(millis);
         }
      catch (InterruptedException e)
         {
         System.out.println("Error while sleeping: " + e);
         }
      }

   private RobosapienDance()
      {
      // private to prevent instantiation
      }
   }
