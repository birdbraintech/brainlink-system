package edu.cmu.ri.createlab.brainlink.robots.roomba;

import edu.cmu.ri.createlab.brainlink.BrainLink;
import edu.cmu.ri.createlab.brainlink.commands.IRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.robots.BaseBrainLinkControllable;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 * @author Tom Lauwers (tlauwers@birdbraintechnologies.com)
 */
public final class Roomba extends BaseBrainLinkControllable
   {
   private static final Logger LOG = Logger.getLogger(Roomba.class);

   public Roomba()
      {
      this(null);
      }

   public Roomba(final String serialPortNames)
      {
      super(serialPortNames);

      final BrainLink brainLink = getBrainLink();
      if (brainLink != null)
         {
         LOG.debug("Roomba.Roomba(): initializing IR for Roomba");
         brainLink.initializeIR(RoombaConstants.getInitializationCommand());
         }
      }

   public boolean turnLeft()
      {
      return executeCommandStrategy(RoombaConstants.CommandStrategies.TURN_LEFT_COMMAND_STRATEGY);
      }

   public boolean turnRight()
      {
      return executeCommandStrategy(RoombaConstants.CommandStrategies.TURN_RIGHT_COMMAND_STRATEGY);
      }

   public boolean driveForward()
      {
      return executeCommandStrategy(RoombaConstants.CommandStrategies.DRIVE_FORWARD_COMMAND_STRATEGY);
      }

   public boolean clean()
      {
      return executeCommandStrategy(RoombaConstants.CommandStrategies.CLEAN_COMMAND_STRATEGY);
      }

   public boolean max()
      {
      return executeCommandStrategy(RoombaConstants.CommandStrategies.MAX_COMMAND_STRATEGY);
      }
  public boolean spot()
      {
      return executeCommandStrategy(RoombaConstants.CommandStrategies.SPOT_COMMAND_STRATEGY);
      }
  public boolean powerOff()
      {
      return executeCommandStrategy(RoombaConstants.CommandStrategies.POWER_OFF_COMMAND_STRATEGY);
      }
  public boolean dock()
      {
      return executeCommandStrategy(RoombaConstants.CommandStrategies.DOCK_COMMAND_STRATEGY);
      }
  public boolean stop()
      {
      return executeCommandStrategy(RoombaConstants.CommandStrategies.STOP_COMMAND_STRATEGY);
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
