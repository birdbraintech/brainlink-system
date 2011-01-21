package edu.cmu.ri.createlab.brainlink.robots.prime8;

import edu.cmu.ri.createlab.brainlink.BrainLink;
import edu.cmu.ri.createlab.brainlink.commands.IRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.robots.BaseBrainLinkControllable;
import org.apache.log4j.Logger;

/**
 * @author Tom Lauwers (tlauwers@birdbraintechnologies.com)
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class Prime8 extends BaseBrainLinkControllable
   {
   private static final Logger LOG = Logger.getLogger(Prime8.class);

   public Prime8()
      {
      this(null);
      }

   public Prime8(final String serialPortNames)
      {
      super(serialPortNames);

      final BrainLink brainLink = getBrainLink();
      if (brainLink != null)
         {
         LOG.debug("Prime8.Prime8(): initializing IR for Prime8");
         brainLink.initializeIR(Prime8Constants.getInitializationCommand());
         }
      }

   public boolean turnLeft()
      {
      return executeCommandStrategy(Prime8Constants.CommandStrategies.TURN_LEFT_COMMAND_STRATEGY);
      }

   public boolean turnRight()
      {
      return executeCommandStrategy(Prime8Constants.CommandStrategies.TURN_RIGHT_COMMAND_STRATEGY);
      }

   public boolean walkForward()
      {
      return executeCommandStrategy(Prime8Constants.CommandStrategies.WALK_FORWARD_COMMAND_STRATEGY);
      }

   public boolean walkBackward()
      {
      return executeCommandStrategy(Prime8Constants.CommandStrategies.WALK_BACKWARD_COMMAND_STRATEGY);
      }

   public boolean stop()
      {
      return executeCommandStrategy(Prime8Constants.CommandStrategies.STOP_COMMAND_STRATEGY);
      }

   public boolean standUp()
      {
      return executeCommandStrategy(Prime8Constants.CommandStrategies.STAND_UP_COMMAND_STRATEGY);
      }

   public boolean sitDown()
      {
      return executeCommandStrategy(Prime8Constants.CommandStrategies.SIT_DOWN_COMMAND_STRATEGY);
      }

   public boolean growl()
      {
      return executeCommandStrategy(Prime8Constants.CommandStrategies.GROWL_COMMAND_STRATEGY);
      }

   public boolean guard()
      {
      return executeCommandStrategy(Prime8Constants.CommandStrategies.GUARD_COMMAND_STRATEGY);
      }

   public boolean shoot()
      {
      return executeCommandStrategy(Prime8Constants.CommandStrategies.SHOOT_COMMAND_STRATEGY);
      }

   public boolean goodGorilla()
      {
      return executeCommandStrategy(Prime8Constants.CommandStrategies.GOOD_GORILLA_COMMAND_STRATEGY);
      }

   public boolean demo()
      {
      return executeCommandStrategy(Prime8Constants.CommandStrategies.DEMO_COMMAND_STRATEGY);
      }

   public boolean autonomousMode()
      {
      return executeCommandStrategy(Prime8Constants.CommandStrategies.AUTONOMOUS_COMMAND_STRATEGY);
      }

   public boolean animationMode()
      {
      return executeCommandStrategy(Prime8Constants.CommandStrategies.ANIMATION_COMMAND_STRATEGY);
      }

   private boolean executeCommandStrategy(final IRCommandStrategy stopCommandStrategy)
      {
      final BrainLink brainLink = getBrainLink();
      return brainLink != null && brainLink.sendIRCommand(stopCommandStrategy);
      }

   protected void prepareForDisconnect()
      {
      stop();
      }
   }
