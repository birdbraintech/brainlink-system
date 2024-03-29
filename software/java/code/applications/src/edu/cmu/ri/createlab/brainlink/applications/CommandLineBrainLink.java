package edu.cmu.ri.createlab.brainlink.applications;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import edu.cmu.ri.createlab.brainlink.BrainLink;
import edu.cmu.ri.createlab.brainlink.BrainLinkConstants;
import edu.cmu.ri.createlab.brainlink.BrainLinkInterface;
import edu.cmu.ri.createlab.device.CreateLabDevicePingFailureEventListener;
import edu.cmu.ri.createlab.serial.commandline.SerialDeviceCommandLineApplication;
import edu.cmu.ri.createlab.util.ArrayUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class CommandLineBrainLink extends SerialDeviceCommandLineApplication
   {
   private static final Logger LOG = Logger.getLogger(CommandLineBrainLink.class);
   private static final int THIRTY_SECONDS_IN_MILLIS = 30000;

   public static void main(final String[] args)
      {
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      new CommandLineBrainLink(in).run();
      }

   private BrainLinkInterface brainLink;

   private CommandLineBrainLink(final BufferedReader in)
      {
      super(in);

      registerActions();
      }

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
            if (isConnected())
               {
               println("You are already connected to a BrainLink.");
               }
            else
               {
               println("Connecting to the BrainLink...");
               brainLink = new BrainLink();
               brainLink.addCreateLabDevicePingFailureEventListener(
                     new CreateLabDevicePingFailureEventListener()
                     {
                     public void handlePingFailureEvent()
                        {
                        println("BrainLink ping failure detected.  You will need to reconnect.");
                        brainLink = null;
                        }
                     });

               println("Connection successful!");
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
            if (isConnected())
               {
               println("Battery Voltage: " + brainLink.getBatteryVoltage());
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
            if (isConnected())
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

               brainLink.setFullColorLED(r, g, b);
               }
            else
               {
               println("You must be connected to the BrainLink first.");
               }
            }
         };

   private final Runnable getPhotoresistorStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isConnected())
               {
               println(convertPhotoresistorStateToString());
               }
            else
               {
               println("You must be connected to the BrainLink first.");
               }
            }
         };

   private final Runnable pollingGetPhotoresistorStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isConnected())
               {
               poll(
                     new Runnable()
                     {
                     public void run()
                        {
                        println(convertPhotoresistorStateToString());
                        }
                     });
               }
            else
               {
               println("You must be connected to the BrainLink first.");
               }
            }
         };

   private final Runnable getAccelerometerStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isConnected())
               {
               println(convertAccelerometerStateToString());
               }
            else
               {
               println("You must be connected to the BrainLink first.");
               }
            }
         };

   private final Runnable getWasShakenAction =
         new Runnable()
         {
         public void run()
            {
            if (isConnected())
               {
               println(getWasShakenAsString());
               }
            else
               {
               println("You must be connected to the BrainLink first.");
               }
            }
         };

   private final Runnable getWasTappedAction =
         new Runnable()
         {
         public void run()
            {
            if (isConnected())
               {
               println(getWasTappedAsString());
               }
            else
               {
               println("You must be connected to the BrainLink first.");
               }
            }
         };

   private final Runnable getWasShakenOrTappedAction =
         new Runnable()
         {
         public void run()
            {
            if (isConnected())
               {
               println(getWasShakenOrTappedAsString());
               }
            else
               {
               println("You must be connected to the BrainLink first.");
               }
            }
         };

   private final Runnable pollingGetAccelerometerStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isConnected())
               {
               poll(
                     new Runnable()
                     {
                     public void run()
                        {
                        println(convertAccelerometerStateToString());
                        }
                     });
               }
            else
               {
               println("You must be connected to the BrainLink first.");
               }
            }
         };

   private final Runnable getAnalogInputsAction =
         new Runnable()
         {
         public void run()
            {
            if (isConnected())
               {
               println(convertAnalogInputsToString());
               }
            else
               {
               println("You must be connected to the BrainLink first.");
               }
            }
         };

   private final Runnable playToneAction =
         new Runnable()
         {
         public void run()
            {
            if (isConnected())
               {
               final Integer frequency = readInteger("Frequency (hz) [" + BrainLinkConstants.TONE_MIN_FREQUENCY + ", " + BrainLinkConstants.TONE_MAX_FREQUENCY + "]: ");
               if (frequency == null || frequency < BrainLinkConstants.TONE_MIN_FREQUENCY || frequency > BrainLinkConstants.TONE_MAX_FREQUENCY)
                  {
                  println("Invalid frequency");
                  return;
                  }

               brainLink.playTone(frequency);
               }
            else
               {
               println("You must be connected to the BrainLink first.");
               }
            }
         };

   private final Runnable turnOffSpeakerAction =
         new Runnable()
         {
         public void run()
            {
            if (isConnected())
               {
               brainLink.turnOffSpeaker();
               }
            else
               {
               println("You must be connected to the BrainLink first.");
               }
            }
         };

   private final Runnable turnOffIRAction =
         new Runnable()
         {
         public void run()
            {
            if (isConnected())
               {
               brainLink.turnOffIR();
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

   private void registerActions()
      {
      registerAction("?", enumeratePortsAction);
      registerAction("c", connectToBrainlinkAction);
      registerAction("d", disconnectFromBrainlinkAction);
      registerAction("b", getBatteryVoltageAction);
      registerAction("f", setFullColorLEDAction);
      registerAction("a", getAccelerometerStateAction);

      registerAction("S", getWasShakenAction);
      registerAction("T", getWasTappedAction);
      registerAction("U", getWasShakenOrTappedAction);

      registerAction("A", pollingGetAccelerometerStateAction);
      registerAction("l", getPhotoresistorStateAction);
      registerAction("L", pollingGetPhotoresistorStateAction);
      registerAction("n", getAnalogInputsAction);
      registerAction("t", playToneAction);
      registerAction("s", turnOffSpeakerAction);
      registerAction("i", turnOffIRAction);

      registerAction(QUIT_COMMAND, quitAction);
      }

   protected final void menu()
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
      println("a         Get the accelerometer state");
      println("A         Continuously poll the accelerometer for 30 seconds");
      println("S         Returns whether the BrainLink was shaken since last accelerometer read");
      println("T         Returns whether the BrainLink was tapped since last accelerometer read");
      println("U         Returns whether the BrainLink was shaken or tapped since last accelerometer read");
      println("l         Get the state of the photoresistor");
      println("L         Continuously poll the photoresistor for 30 seconds");
      println("n         Get the analog input values");
      println("");
      println("t         Play a tone through the BrainLink's speaker");
      println("s         Turn off the speaker");
      println("i         Turn off IR");
      println("");
      println("q         Quit");
      println("");
      println("--------------------------------------------");
      }

   @SuppressWarnings({"BusyWait"})
   protected final void poll(final Runnable strategy)
      {
      final long startTime = System.currentTimeMillis();
      while (isConnected() && System.currentTimeMillis() - startTime < THIRTY_SECONDS_IN_MILLIS)
         {
         strategy.run();
         try
            {
            Thread.sleep(30);
            }
         catch (InterruptedException e)
            {
            LOG.error("InterruptedException while sleeping", e);
            }
         }
      }

   private String getWasShakenAsString()
      {
      final Boolean wasShaken = brainLink.wasShaken();
      if (wasShaken != null)
         {
         return "Was shaken: " + wasShaken;
         }
      return "Accelerometer: failed to read value";
      }

   private String getWasTappedAsString()
      {
      final Boolean wasTapped = brainLink.wasTapped();
      if (wasTapped != null)
         {
         return "Was tapped: " + wasTapped;
         }
      return "Accelerometer: failed to read value";
      }

   private String getWasShakenOrTappedAsString()
      {
      final Boolean wasShakenOrTapped = brainLink.wasShakenOTapped();
      if (wasShakenOrTapped != null)
         {
         return "Was shaken or tapped: " + wasShakenOrTapped;
         }
      return "Accelerometer: failed to read value";
      }

   private String convertAccelerometerStateToString()
      {
      final double[] accelerometer = brainLink.getAccelerometerValuesInGs();
      if (accelerometer != null)
         {
         return "Accelerometer: " + ArrayUtils.arrayToString(accelerometer);
         }

      return "Accelerometer: failed to read value";
      }

   private String convertPhotoresistorStateToString()
      {
      final Integer photoresistor = brainLink.getLightSensor();
      if (photoresistor != null)
         {
         return "Photoresistor: " + photoresistor;
         }

      return "Photoresistor: failed to read value";
      }

   private String convertAnalogInputsToString()
      {
      final int[] analogInputs = brainLink.getAnalogInputs();
      if (analogInputs != null)
         {
         return "Analog Inputs: " + ArrayUtils.arrayToString(analogInputs);
         }

      return "Analog Inputs: failed to read value";
      }

   protected final boolean isConnected()
      {
      return brainLink != null;
      }

   protected final void disconnect()
      {
      if (isConnected())
         {
         brainLink.disconnect();
         brainLink = null;
         }
      }
   }
