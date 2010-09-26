package edu.cmu.ri.createlab.brainlink;

import java.io.BufferedReader;
import java.util.SortedMap;
import edu.cmu.ri.createlab.serial.commandline.SerialDeviceCommandLineApplication;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"FieldCanBeLocal"})
public abstract class BaseCommandLineBrainLink extends SerialDeviceCommandLineApplication
   {
   private final Runnable enumeratePortsAction =
         new Runnable()
         {
         public void run()
            {
            enumeratePorts();
            }
         };

   private final Runnable connectToBrainlinkAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               println("You are already connected to a BrainLink.");
               }
            else
               {
               final SortedMap<Integer, String> portMap = enumeratePorts();

               if (!portMap.isEmpty())
                  {
                  final Integer index = readInteger("Connect to port number: ");

                  if (index == null)
                     {
                     println("Invalid port");
                     }
                  else
                     {
                     final String serialPortName = portMap.get(index);

                     if (serialPortName != null)
                        {
                        if (!connect(serialPortName))
                           {
                           println("Connection failed!");
                           }
                        }
                     else
                        {
                        println("Invalid port");
                        }
                     }
                  }
               }
            }
         };

   private final Runnable disconnectFromBrainlinkAction =
         new Runnable()
         {
         public void run()
            {
            disconnect();
            }
         };

   private final Runnable getBatteryVoltageAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               println("Battery Voltage: " + getBatteryVoltage());
               }
            else
               {
               println("You must be connected to the BrainLink first.");
               }
            }
         };

   private final Runnable setFullColorLEDAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final Integer r = readInteger("Red Intensity   [" + BrainLinkConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY + ", " + BrainLinkConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY + "]: ");
               if (r == null || r < BrainLinkConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY || r > BrainLinkConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY)
                  {
                  println("Invalid red intensity");
                  return;
                  }
               final Integer g = readInteger("Green Intensity [" + BrainLinkConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY + ", " + BrainLinkConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY + "]: ");
               if (g == null || g < BrainLinkConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY || g > BrainLinkConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY)
                  {
                  println("Invalid green intensity");
                  return;
                  }
               final Integer b = readInteger("Blue Intensity  [" + BrainLinkConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY + ", " + BrainLinkConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY + "]: ");
               if (b == null || b < BrainLinkConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY || b > BrainLinkConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY)
                  {
                  println("Invalid blue intensity");
                  return;
                  }

               setFullColorLED(r, g, b);
               }
            else
               {
               println("You must be connected to the BrainLink first.");
               }
            }
         };

   private final Runnable quitAction =
         new Runnable()
         {
         public void run()
            {
            disconnect();
            println("Bye!");
            }
         };

   protected BaseCommandLineBrainLink(final BufferedReader in)
      {
      super(in);

      registerAction("?", enumeratePortsAction);
      registerAction("c", connectToBrainlinkAction);
      registerAction("d", disconnectFromBrainlinkAction);
      registerAction("b", getBatteryVoltageAction);
      registerAction("f", setFullColorLEDAction);
      registerAction(QUIT_COMMAND, quitAction);
      }

   protected void menu()
      {
      println("COMMANDS -----------------------------------");
      println("");
      println("?         List all available serial ports");
      println("");
      println("c         Connect to the BrainLink");
      println("d         Disconnect from the BrainLink");
      println("");
      println("b         Get the battery voltage");
      println("f         Control the full-color LED");
      println("");
      println("q         Quit");
      println("");
      println("--------------------------------------------");
      }

   protected abstract boolean connect(final String serialPortName);

   protected abstract Integer getBatteryVoltage();

   protected abstract void setFullColorLED(final int r, final int g, final int b);

   protected abstract boolean isInitialized();
   }
