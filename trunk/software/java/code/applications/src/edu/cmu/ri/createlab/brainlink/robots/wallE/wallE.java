package edu.cmu.ri.createlab.brainlink.robots.wallE;

import edu.cmu.ri.createlab.brainlink.BrainLink;
import edu.cmu.ri.createlab.brainlink.commands.IRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.robots.BaseBrainLinkControllable;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 * @author Tom Lauwers (tlauwers@birdbraintechnologies.com)
 */
public final class wallE extends BaseBrainLinkControllable
   {
   private static final Logger LOG = Logger.getLogger(wallE.class);

   public wallE()
      {
      this(null);
      }

   public wallE(final String serialPortNames)
      {
      super(serialPortNames);

      final BrainLink brainLink = getBrainLink();
      if (brainLink != null)
         {
         LOG.debug("WALL-E.wallE(): initializing IR for WALL-E");
         brainLink.initializeIR(wallEConstants.getInitializationCommand());
         }
      }

   public boolean turnLeft()
      {
      return executeCommandStrategy(wallEConstants.CommandStrategies.TURN_LEFT_COMMAND_STRATEGY);
      }

   public boolean driveForward()
      {
      return executeCommandStrategy(wallEConstants.CommandStrategies.DRIVE_FORWARD_COMMAND_STRATEGY);
      }

   public boolean smallLeftTurn()
      {
      return executeCommandStrategy(wallEConstants.CommandStrategies.SMALL_LEFT_TURN_COMMAND_STRATEGY);
      }

   public boolean mediumLeftTurn()
      {
      return executeCommandStrategy(wallEConstants.CommandStrategies.MEDIUM_LEFT_TURN_COMMAND_STRATEGY);
      }
   public boolean largeLeftTurn()
      {
      return executeCommandStrategy(wallEConstants.CommandStrategies.LARGE_LEFT_TURN_COMMAND_STRATEGY);
      }

   public boolean butterfly()
      {
      return executeCommandStrategy(wallEConstants.CommandStrategies.BUTTERFLY_COMMAND_STRATEGY);
      }
   public boolean music()
      {
      return executeCommandStrategy(wallEConstants.CommandStrategies.MUSIC_COMMAND_STRATEGY);
      }

   public boolean sun()
      {
      return executeCommandStrategy(wallEConstants.CommandStrategies.SUN_COMMAND_STRATEGY);
      }
   public boolean specialTurn()
      {
      return executeCommandStrategy(wallEConstants.CommandStrategies.SPECIAL_TURN_COMMAND_STRATEGY);
      }

   public boolean talkCloud()
      {
      return executeCommandStrategy(wallEConstants.CommandStrategies.TALK_CLOUD_COMMAND_STRATEGY);
      }
   public boolean goStop()
      {
      return executeCommandStrategy(wallEConstants.CommandStrategies.GO_STOP_COMMAND_STRATEGY);
      }

   public boolean box()
      {
      return executeCommandStrategy(wallEConstants.CommandStrategies.BOX_COMMAND_STRATEGY);
      }
   public boolean prog()
      {
      return executeCommandStrategy(wallEConstants.CommandStrategies.PROG_COMMAND_STRATEGY);
      }

   public boolean stop()
      {
      final BrainLink brainLink = getBrainLink();
      return brainLink != null && brainLink.turnOffIR();
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
