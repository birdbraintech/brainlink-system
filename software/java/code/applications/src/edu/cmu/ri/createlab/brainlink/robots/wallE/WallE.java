package edu.cmu.ri.createlab.brainlink.robots.wallE;

import edu.cmu.ri.createlab.brainlink.BrainLink;
import edu.cmu.ri.createlab.brainlink.commands.IRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.robots.BaseBrainLinkControllable;
import org.apache.log4j.Logger;

/**
 * @author Tom Lauwers (tlauwers@birdbraintechnologies.com)
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class WallE extends BaseBrainLinkControllable
   {
   private static final Logger LOG = Logger.getLogger(WallE.class);

   public WallE()
      {
      this(null);
      }

   /**  Instantiates a WALL-E class
    *
    * @param serialPortNames The serial port identified of the Brainlink connected to this computer
    */
   public WallE(final String serialPortNames)
      {
      super(serialPortNames);

      final BrainLink brainLink = getBrainLink();
      if (brainLink != null)
         {
         LOG.debug("WALL-E.WallE(): initializing IR for WALL-E");
         brainLink.initializeIR(WallEConstants.getInitializationCommand());
         }
      }

   /** WALL-E will turn left until another movement command is send
    *
    * @return true if the call was made successfully, false otherwise
    */
   public boolean turnLeft()
      {
      return executeCommandStrategy(WallEConstants.CommandStrategies.TURN_LEFT_COMMAND_STRATEGY);
      }

   /** WALL-E will drive forward until another movement command is send
    *
    * @return true if the call was made successfully, false otherwise
    */
   public boolean driveForward()
      {
      return executeCommandStrategy(WallEConstants.CommandStrategies.DRIVE_FORWARD_COMMAND_STRATEGY);
      }

   /** WALL-E will turn left roughly 45 degrees
    *
    * @return true if the call was made successfully, false otherwise
    */
   public boolean smallLeftTurn()
      {
      return executeCommandStrategy(WallEConstants.CommandStrategies.SMALL_LEFT_TURN_COMMAND_STRATEGY);
      }

   /** WALL-E will turn left roughly 90 degrees
    *
    * @return true if the call was made successfully, false otherwise
    */
   public boolean mediumLeftTurn()
      {
      return executeCommandStrategy(WallEConstants.CommandStrategies.MEDIUM_LEFT_TURN_COMMAND_STRATEGY);
      }

   /** WALL-E will turn left roughly 180 degrees
    *
    * @return true if the call was made successfully, false otherwise
    */
   public boolean largeLeftTurn()
      {
      return executeCommandStrategy(WallEConstants.CommandStrategies.LARGE_LEFT_TURN_COMMAND_STRATEGY);
      }

   public boolean butterfly()
      {
      return executeCommandStrategy(WallEConstants.CommandStrategies.BUTTERFLY_COMMAND_STRATEGY);
      }

   public boolean music()
      {
      return executeCommandStrategy(WallEConstants.CommandStrategies.MUSIC_COMMAND_STRATEGY);
      }

   public boolean sun()
      {
      return executeCommandStrategy(WallEConstants.CommandStrategies.SUN_COMMAND_STRATEGY);
      }

   /** WALL-E will turn and dance
    *
    * @return true if the call was made successfully, false otherwise
    */
   public boolean specialTurn()
      {
      return executeCommandStrategy(WallEConstants.CommandStrategies.SPECIAL_TURN_COMMAND_STRATEGY);
      }

   public boolean talkCloud()
      {
      return executeCommandStrategy(WallEConstants.CommandStrategies.TALK_CLOUD_COMMAND_STRATEGY);
      }

   /** WALL-E will execute a program
    *
    * @return true if the call was made successfully, false otherwise
    */
   public boolean goStop()
      {
      return executeCommandStrategy(WallEConstants.CommandStrategies.GO_STOP_COMMAND_STRATEGY);
      }

   public boolean box()
      {
      return executeCommandStrategy(WallEConstants.CommandStrategies.BOX_COMMAND_STRATEGY);
      }

   /** WALL-E will enter a programming mode that accepts commands.  R
    *
    * @return true if the call was made successfully, false otherwise
    */
   public boolean prog()
      {
      return executeCommandStrategy(WallEConstants.CommandStrategies.PROG_COMMAND_STRATEGY);
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
