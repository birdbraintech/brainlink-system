package edu.cmu.ri.createlab.brainlink.robots.robosapien;

import edu.cmu.ri.createlab.brainlink.BrainLink;
import edu.cmu.ri.createlab.brainlink.commands.SimpleIRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.robots.BaseBrainLinkControllable;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class Robosapien extends BaseBrainLinkControllable
   {
   private static final Logger LOG = Logger.getLogger(Robosapien.class);

   public Robosapien()
      {
      final BrainLink brainLink = getBrainLink();
      if (brainLink != null)
         {
         LOG.debug("Robosapien.Robosapien(): initializing IR for Robosapien");
         brainLink.initializeIR(RobosapienConstants.getInitializationCommand());
         }
      }

   public boolean leftArmUp()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.LEFT_ARM_UP_COMMAND_STRATEGY);
      }

   public boolean leftArmDown()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.LEFT_ARM_DOWN_COMMAND_STRATEGY);
      }

   public boolean leftArmIn()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.LEFT_ARM_IN_COMMAND_STRATEGY);
      }

   public boolean leftArmOut()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.LEFT_ARM_OUT_COMMAND_STRATEGY);
      }

   public boolean leftHandThump()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.LEFT_HAND_THUMP_COMMAND_STRATEGY);
      }

   public boolean leftHandThrow()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.LEFT_HAND_THROW_COMMAND_STRATEGY);
      }

   public boolean leftHandPickup()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.LEFT_HAND_PICKUP_COMMAND_STRATEGY);
      }

   public boolean leftHandStrike1()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.LEFT_HAND_STRIKE_1_COMMAND_STRATEGY);
      }

   public boolean leftHandStrike2()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.LEFT_HAND_STRIKE_2_COMMAND_STRATEGY);
      }

   public boolean leftHandStrike3()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.LEFT_HAND_STRIKE_3_COMMAND_STRATEGY);
      }

   public boolean leftHandSweep()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.LEFT_HAND_SWEEP_COMMAND_STRATEGY);
      }

   public boolean rightArmUp()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.RIGHT_ARM_UP_COMMAND_STRATEGY);
      }

   public boolean rightArmDown()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.RIGHT_ARM_DOWN_COMMAND_STRATEGY);
      }

   public boolean rightArmIn()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.RIGHT_ARM_IN_COMMAND_STRATEGY);
      }

   public boolean rightArmOut()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.RIGHT_ARM_OUT_COMMAND_STRATEGY);
      }

   public boolean rightHandThump()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.RIGHT_HAND_THUMP_COMMAND_STRATEGY);
      }

   public boolean rightHandThrow()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.RIGHT_HAND_THROW_COMMAND_STRATEGY);
      }

   public boolean rightHandPickup()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.RIGHT_HAND_PICKUP_COMMAND_STRATEGY);
      }

   public boolean rightHandStrike1()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.RIGHT_HAND_STRIKE_1_COMMAND_STRATEGY);
      }

   public boolean rightHandStrike2()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.RIGHT_HAND_STRIKE_2_COMMAND_STRATEGY);
      }

   public boolean rightHandStrike3()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.RIGHT_HAND_STRIKE_3_COMMAND_STRATEGY);
      }

   public boolean rightHandSweep()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.RIGHT_HAND_SWEEP_COMMAND_STRATEGY);
      }

   public boolean turnLeft()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.TURN_LEFT_COMMAND_STRATEGY);
      }

   public boolean turnRight()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.TURN_RIGHT_COMMAND_STRATEGY);
      }

   public boolean tiltBodyLeft()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.TILT_BODY_LEFT_COMMAND_STRATEGY);
      }

   public boolean tiltBodyRight()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.TILT_BODY_RIGHT_COMMAND_STRATEGY);
      }

   public boolean leanForward()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.LEAN_FORWARD_COMMAND_STRATEGY);
      }

   public boolean leanBackward()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.LEAN_BACKWARD_COMMAND_STRATEGY);
      }

   public boolean leftTurnStep()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.LEFT_TURN_STEP_COMMAND_STRATEGY);
      }

   public boolean rightTurnStep()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.RIGHT_TURN_STEP_COMMAND_STRATEGY);
      }

   public boolean forwardStep()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.FORWARD_STEP_COMMAND_STRATEGY);
      }

   public boolean backwardStep()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.BACKWARD_STEP_COMMAND_STRATEGY);
      }

   public boolean walkForward()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.WALK_FORWARD_COMMAND_STRATEGY);
      }

   public boolean walkBackward()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.WALK_BACKWARD_COMMAND_STRATEGY);
      }

   public boolean stop()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.STOP_COMMAND_STRATEGY);
      }

   public boolean highFive()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.HIGH_FIVE_COMMAND_STRATEGY);
      }

   public boolean bulldozer()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.BULLDOZER_COMMAND_STRATEGY);
      }

   public boolean feetShuffle()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.FEET_SHUFFLE_COMMAND_STRATEGY);
      }

   public boolean raiseArmThrow()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.RAISE_ARM_THROW_COMMAND_STRATEGY);
      }

   public boolean karateChop()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.KARATE_CHOP_COMMAND_STRATEGY);
      }

   public boolean burp()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.BURP_COMMAND_STRATEGY);
      }

   public boolean roar()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.ROAR_COMMAND_STRATEGY);
      }

   public boolean oops()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.OOPS_COMMAND_STRATEGY);
      }

   public boolean whistle()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.WHISTLE_COMMAND_STRATEGY);
      }

   public boolean talkback()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.TALKBACK_COMMAND_STRATEGY);
      }

   public boolean sleep()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.SLEEP_COMMAND_STRATEGY);
      }

   public boolean wakeUp()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.WAKEUP_COMMAND_STRATEGY);
      }

   public boolean listen()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.LISTEN_COMMAND_STRATEGY);
      }

   public boolean reset()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.RESET_COMMAND_STRATEGY);
      }

   public boolean allDemo()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.ALL_DEMO_COMMAND_STRATEGY);
      }

   public boolean karateDemo()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.KARATE_DEMO_COMMAND_STRATEGY);
      }

   public boolean rudeDemo()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.RUDE_DEMO_COMMAND_STRATEGY);
      }

   public boolean dance()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.DANCE_COMMAND_STRATEGY);
      }

   public boolean doNothing()
      {
      return executeCommandStrategy(RobosapienConstants.CommandStrategies.NOTHING_COMMAND_STRATEGY);
      }

   private boolean executeCommandStrategy(final SimpleIRCommandStrategy stopCommandStrategy)
      {
      final BrainLink brainLink = getBrainLink();
      return brainLink != null && brainLink.sendSimpleIRCommand(stopCommandStrategy);
      }

   protected void prepareForDisconnect()
      {
      stop();
      }
   }
