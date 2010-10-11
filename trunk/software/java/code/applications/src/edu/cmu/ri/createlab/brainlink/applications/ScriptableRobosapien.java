package edu.cmu.ri.createlab.brainlink.applications;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.cmu.ri.createlab.brainlink.robots.robosapien.Robosapien;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public class ScriptableRobosapien
   {
   private static final String COMMENT_CHARACTER = "#";

   private interface RobosapienAction
      {
      boolean execute(final Robosapien robosapien);
      }

   private static final Map<String, RobosapienAction> COMMAND_MAP;
   private static final int SLEEP_INCREMENT_MILLIS = 50;

   static
      {
      final Map<String, RobosapienAction> commandMap = new HashMap<String, RobosapienAction>();

      // yeah, yeah...I know...I could do this more concisely with reflection.
      commandMap.put("leftArmUp",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.leftArmUp();
                        }
                     });

      commandMap.put("leftArmDown",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.leftArmDown();
                        }
                     });

      commandMap.put("leftArmIn",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.leftArmIn();
                        }
                     });

      commandMap.put("leftArmOut",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.leftArmOut();
                        }
                     });

      commandMap.put("leftHandThump",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.leftHandThump();
                        }
                     });

      commandMap.put("leftHandThrow",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.leftHandThrow();
                        }
                     });

      commandMap.put("leftHandPickup",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.leftHandPickup();
                        }
                     });

      commandMap.put("leftHandStrike1",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.leftHandStrike1();
                        }
                     });

      commandMap.put("leftHandStrike2",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.leftHandStrike2();
                        }
                     });

      commandMap.put("leftHandStrike3",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.leftHandStrike3();
                        }
                     });

      commandMap.put("leftHandSweep",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.leftHandSweep();
                        }
                     });

      commandMap.put("rightArmUp",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.rightArmUp();
                        }
                     });

      commandMap.put("rightArmDown",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.rightArmDown();
                        }
                     });

      commandMap.put("rightArmIn",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.rightArmIn();
                        }
                     });

      commandMap.put("rightArmOut",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.rightArmOut();
                        }
                     });

      commandMap.put("rightHandThump",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.rightHandThump();
                        }
                     });

      commandMap.put("rightHandThrow",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.rightHandThrow();
                        }
                     });

      commandMap.put("rightHandPickup",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.rightHandPickup();
                        }
                     });

      commandMap.put("rightHandStrike1",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.rightHandStrike1();
                        }
                     });

      commandMap.put("rightHandStrike2",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.rightHandStrike2();
                        }
                     });

      commandMap.put("rightHandStrike3",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.rightHandStrike3();
                        }
                     });

      commandMap.put("rightHandSweep",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.rightHandSweep();
                        }
                     });

      commandMap.put("turnLeft",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.turnLeft();
                        }
                     });

      commandMap.put("turnRight",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.turnRight();
                        }
                     });

      commandMap.put("tiltBodyLeft",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.tiltBodyLeft();
                        }
                     });

      commandMap.put("tiltBodyRight",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.tiltBodyRight();
                        }
                     });

      commandMap.put("leanForward",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.leanForward();
                        }
                     });

      commandMap.put("leanBackward",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.leanBackward();
                        }
                     });

      commandMap.put("leftTurnStep",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.leftTurnStep();
                        }
                     });

      commandMap.put("rightTurnStep",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.rightTurnStep();
                        }
                     });

      commandMap.put("forwardStep",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.forwardStep();
                        }
                     });

      commandMap.put("backwardStep",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.backwardStep();
                        }
                     });

      commandMap.put("walkForward",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.walkForward();
                        }
                     });

      commandMap.put("walkBackward",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.walkBackward();
                        }
                     });

      commandMap.put("stop",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.stop();
                        }
                     });

      commandMap.put("highFive",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.highFive();
                        }
                     });

      commandMap.put("bulldozer",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.bulldozer();
                        }
                     });

      commandMap.put("feetShuffle",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.feetShuffle();
                        }
                     });

      commandMap.put("raiseArmThrow",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.raiseArmThrow();
                        }
                     });

      commandMap.put("karateChop",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.karateChop();
                        }
                     });

      commandMap.put("burp",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.burp();
                        }
                     });

      commandMap.put("roar",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.roar();
                        }
                     });

      commandMap.put("oops",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.oops();
                        }
                     });

      commandMap.put("whistle",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.whistle();
                        }
                     });

      commandMap.put("talkback",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.talkback();
                        }
                     });

      commandMap.put("sleep",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.sleep();
                        }
                     });

      commandMap.put("wakeUp",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.wakeUp();
                        }
                     });

      commandMap.put("listen",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.listen();
                        }
                     });

      commandMap.put("reset",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.reset();
                        }
                     });

      commandMap.put("allDemo",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.allDemo();
                        }
                     });

      commandMap.put("karateDemo",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.karateDemo();
                        }
                     });

      commandMap.put("rudeDemo",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.rudeDemo();
                        }
                     });

      commandMap.put("dance",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.dance();
                        }
                     });

      commandMap.put("doNothing",
                     new RobosapienAction()
                     {
                     public boolean execute(final Robosapien robosapien)
                        {
                        return robosapien.doNothing();
                        }
                     });

      COMMAND_MAP = Collections.unmodifiableMap(commandMap);
      }

   public static void main(final String[] args) throws IOException
      {
      if (args.length < 1)
         {
         System.err.println("ERROR: no script file specifed");
         System.exit(1);
         }

      final File scriptFile = new File(args[0]);
      if (scriptFile.isFile())
         {
         // read script to get the set of commands
         final BufferedReader reader = new BufferedReader(new FileReader(scriptFile));
         String line;
         final List<String> commands = new ArrayList<String>();
         while ((line = reader.readLine()) != null)
            {
            commands.add(line);
            }

         // now loop over the commands and execute them
         if (commands.size() > 0)
            {
            executeCommands(commands);
            }
         }
      else
         {
         System.err.println("ERROR: could not find or read the file [" + scriptFile + "]");
         }
      }

   private static void executeCommands(final List<String> commands)
      {
      final Robosapien robosapien = new Robosapien();

      System.out.println("");
      System.out.println("Press ENTER to quit.");

      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      boolean shouldQuit = false;
      while (!shouldQuit)
         {
         for (final String command : commands)
            {
            if (command != null)
               {
               final String trimmedCommand = command.trim();
               if (trimmedCommand.length() > 0 && !trimmedCommand.startsWith(COMMENT_CHARACTER))
                  {
                  // try to look up the command in the map
                  final RobosapienAction action = COMMAND_MAP.get(trimmedCommand);

                  // if the action is null, then it's either unknown or a number specifying the milliseconds to wait
                  if (action == null)
                     {
                     try
                        {
                        // try to parse the command as a long
                        final Long sleepDurationMillis = Long.parseLong(trimmedCommand);

                        // sleep for the specified milliseconds (should check for user input during the sleep--see Finch sleep)
                        System.out.println("Pausing for " + sleepDurationMillis + " ms");
                        if (sleep(in, sleepDurationMillis))
                           {
                           shouldQuit = true;
                           break;
                           }
                        }
                     catch (NumberFormatException e)
                        {
                        System.err.println("NumberFormatException while trying to parse [" + trimmedCommand + "] as a number.  Ignoring it.");
                        }
                     }
                  else
                     {
                     System.out.println("Executing action: " + trimmedCommand);
                     action.execute(robosapien);
                     }
                  }
               }
            }

         if (!shouldQuit && shouldQuit(in))
            {
            shouldQuit = true;
            }
         }

      robosapien.disconnect();
      }

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

   private ScriptableRobosapien()
      {
      // private to prevent instantiation
      }
   }
