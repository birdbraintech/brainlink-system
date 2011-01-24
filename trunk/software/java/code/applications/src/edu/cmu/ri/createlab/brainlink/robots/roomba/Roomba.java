package edu.cmu.ri.createlab.brainlink.robots.roomba;

import edu.cmu.ri.createlab.brainlink.BrainLink;
import edu.cmu.ri.createlab.brainlink.commands.IRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.robots.BaseBrainLinkControllable;
import org.apache.log4j.Logger;

/**
 * @author Tom Lauwers (tlauwers@birdbraintechnologies.com)
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class Roomba extends BaseBrainLinkControllable
   {
   private static final Logger LOG = Logger.getLogger(Roomba.class);

   public Roomba()
      {
      this(null);
      }

       /** Instantiates a Roomba class
        *
        * @param serialPortNames the serial port identifier on which the Brainlink can be found
        */
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

       /** Turns roomba left until another movement method is called
        *
        * @return  true if the call was made successfully, false otherwise
        */
   public boolean turnLeft()
      {
      return executeCommandStrategy(RoombaConstants.CommandStrategies.TURN_LEFT_COMMAND_STRATEGY);
      }
        /** Turns roomba right until another movement method is called
        *
        * @return  true if the call was made successfully, false otherwise
        */
   public boolean turnRight()
      {
      return executeCommandStrategy(RoombaConstants.CommandStrategies.TURN_RIGHT_COMMAND_STRATEGY);
      }
       /** Drives roomba forward until another movement method is called
        *
        * @return  true if the call was made successfully, false otherwise
        */
   public boolean driveForward()
      {
      return executeCommandStrategy(RoombaConstants.CommandStrategies.DRIVE_FORWARD_COMMAND_STRATEGY);
      }
      /** Sends the roomba clean command
        *
        * @return  true if the call was made successfully, false otherwise
        */
   public boolean clean()
      {
      return executeCommandStrategy(RoombaConstants.CommandStrategies.CLEAN_COMMAND_STRATEGY);
      }
       /** Tells roomba to clean until it runs out of batteries
        *
        * @return  true if the call was made successfully, false otherwise
        */
   public boolean max()
      {
      return executeCommandStrategy(RoombaConstants.CommandStrategies.MAX_COMMAND_STRATEGY);
      }
         /** Tells Roomba to do a spot clean
        *
        * @return  true if the call was made successfully, false otherwise
        */
   public boolean spot()
      {
      return executeCommandStrategy(RoombaConstants.CommandStrategies.SPOT_COMMAND_STRATEGY);
      }
       /** Turns off Roomba.  You may need to manually turn roomba back on after sending this command.
        *
        * @return  true if the call was made successfully, false otherwise
        */
   public boolean powerOff()
      {
      return executeCommandStrategy(RoombaConstants.CommandStrategies.POWER_OFF_COMMAND_STRATEGY);
      }

       /** Causes roomba to look for its charging dock
        *
        * @return  true if the call was made successfully, false otherwise
        */
   public boolean dock()
      {
      return executeCommandStrategy(RoombaConstants.CommandStrategies.DOCK_COMMAND_STRATEGY);
      }
       /** Causes Roomba to stop moving
        *
        * @return  true if the call was made successfully, false otherwise
        */
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
