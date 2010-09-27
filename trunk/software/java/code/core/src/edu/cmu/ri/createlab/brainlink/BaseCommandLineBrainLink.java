package edu.cmu.ri.createlab.brainlink;

import java.io.BufferedReader;
import java.util.SortedMap;
import edu.cmu.ri.createlab.serial.commandline.SerialDeviceCommandLineApplication;
import edu.cmu.ri.createlab.util.ArrayUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"FieldCanBeLocal"})
public abstract class BaseCommandLineBrainLink extends SerialDeviceCommandLineApplication
   {
   private static final Logger LOG = Logger.getLogger(BaseCommandLineBrainLink.class);
   private static final int THIRTY_SECONDS_IN_MILLIS = 30000;

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
            if (isConnected())
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

               setFullColorLED(r, g, b);
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

   private final Runnable getThermistorStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isConnected())
               {
               println(convertThermistorStateToString());
               }
            else
               {
               println("You must be connected to the BrainLink first.");
               }
            }
         };

   private final Runnable pollingGetThermistorStateAction =
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
                        println(convertThermistorStateToString());
                        }
                     });
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

               playTone(frequency);
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
               turnOffSpeaker();
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
      registerAction("a", getAccelerometerStateAction);
      registerAction("A", pollingGetAccelerometerStateAction);
      registerAction("l", getPhotoresistorStateAction);
      registerAction("L", pollingGetPhotoresistorStateAction);
      registerAction("n", getAnalogInputsAction);
      registerAction("h", getThermistorStateAction);
      registerAction("H", pollingGetThermistorStateAction);
      registerAction("t", playToneAction);
      registerAction("s", turnOffSpeakerAction);

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
      println("a         Get the accelerometer state");
      println("A         Continuously poll the accelerometer for 30 seconds");
      println("l         Get the state of the photoresistors");
      println("L         Continuously poll the photoresistors for 30 seconds");
      println("n         Get the analog input values");
      println("h         Get the state of the thermistor");
      println("H         Continuously poll the thermistor for 30 seconds");
      println("");
      println("t         Play a tone through the BrainLink's speaker");
      println("s         Turn off the speaker ");
      println("");
      println("q         Quit");
      println("");
      println("--------------------------------------------");
      }

   private void poll(final Runnable strategy)
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

   private String convertAccelerometerStateToString()
      {
      final int[] accelerometer = getAccelerometer();
      if (accelerometer != null)
         {
         return "Accelerometer: " + ArrayUtils.arrayToString(accelerometer);
         }

      return "Accelerometer: failed to read value";
      }

   private String convertPhotoresistorStateToString()
      {
      final int[] photoresistors = getPhotoresistors();
      if (photoresistors != null)
         {
         return "Photoresistors: " + ArrayUtils.arrayToString(photoresistors);
         }

      return "Photoresistors: failed to read value";
      }

   private String convertAnalogInputsToString()
      {
      final int[] analogInputs = getAnalogInputs();
      if (analogInputs != null)
         {
         return "Analog Inputs: " + ArrayUtils.arrayToString(analogInputs);
         }

      return "Analog Inputs: failed to read value";
      }

   private String convertThermistorStateToString()
      {
      final Integer rawValue = getThermistor();
      if (rawValue != null)
         {
         return "Thermistor: " + rawValue;
         }

      return "Thermistor: failed to read value";
      }

   protected abstract boolean connect(final String serialPortName);

   protected abstract boolean isConnected();

   protected abstract Integer getBatteryVoltage();

   protected abstract void setFullColorLED(final int r, final int g, final int b);

   protected abstract int[] getAccelerometer();

   protected abstract int[] getPhotoresistors();

   protected abstract int[] getAnalogInputs();

   protected abstract Integer getThermistor();

   protected abstract void playTone(final int frequency);

   protected abstract void turnOffSpeaker();
   }
