package edu.cmu.ri.createlab.brainlink.robots.TVRemote;

import edu.cmu.ri.createlab.brainlink.BrainLink;
import edu.cmu.ri.createlab.brainlink.commands.IRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.robots.BaseBrainLinkControllable;
import org.apache.log4j.Logger;

/**
 * @author Tom Lauwers (tlauwers@birdbraintechnologies.com)
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class TVRemote extends BaseBrainLinkControllable
   {
   private static final Logger LOG = Logger.getLogger(TVRemote.class);

   public TVRemote()
      {
      this(null);
      }
        /**  Instantiates a Prime8 class
        *
        * @param serialPortNames The serial port identified of the Brainlink connected to this computer
        */
   public TVRemote(final String serialPortNames)
      {
      super(serialPortNames);

      final BrainLink brainLink = getBrainLink();
      if (brainLink != null)
         {
         LOG.debug("TVRemote.TVRemote(): initializing IR for TV");
         brainLink.initializeIR(TVRemoteConstants.getInitializationCommand());
         }
      }

   public boolean power()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.POWER_COMMAND_STRATEGY);
      }

   public boolean zero()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.ZERO_COMMAND_STRATEGY);
      }

   public boolean one()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.ONE_COMMAND_STRATEGY);
      }

   public boolean two()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.TWO_COMMAND_STRATEGY);
      }


   public boolean three()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.THREE_COMMAND_STRATEGY);
      }

   public boolean four()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.FOUR_COMMAND_STRATEGY);
      }


   public boolean five()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.FIVE_COMMAND_STRATEGY);
      }

   public boolean six()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.SIX_COMMAND_STRATEGY);
      }


   public boolean seven()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.SEVEN_COMMAND_STRATEGY);
      }

   public boolean eight()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.EIGHT_COMMAND_STRATEGY);
      }


   public boolean nine()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.NINE_COMMAND_STRATEGY);
      }

   public boolean channelUp()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.CHUP_COMMAND_STRATEGY);
      }


   public boolean channelDown()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.CHDOWN_COMMAND_STRATEGY);
      }

   public boolean volumeUp()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.VOLUP_COMMAND_STRATEGY);
      }


   public boolean volumeDown()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.VOLDOWN_COMMAND_STRATEGY);
      }

   public boolean menu()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.MENU_COMMAND_STRATEGY);
      }


   public boolean plus100()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.PLUS100_COMMAND_STRATEGY);
      }

   public boolean mute()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.MUTE_COMMAND_STRATEGY);
      }


   public boolean display()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.DISPLAY_COMMAND_STRATEGY);
      }

   public boolean play()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.PLAY_COMMAND_STRATEGY);
      }


   public boolean stopVCR()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.STOPVCR_COMMAND_STRATEGY);
      }

   public boolean ffwd()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.FFD_COMMAND_STRATEGY);
      }


   public boolean rewind()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.REW_COMMAND_STRATEGY);
      }


   public boolean game()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.GAME_COMMAND_STRATEGY);
      }

   public boolean tProg()
      {
      return executeCommandStrategy(TVRemoteConstants.CommandStrategies.TPROG_COMMAND_STRATEGY);
      }

    private boolean executeCommandStrategy(final IRCommandStrategy stopCommandStrategy)
      {
      final BrainLink brainLink = getBrainLink();
      return brainLink != null && brainLink.sendIRCommand(stopCommandStrategy);
      }

   protected void prepareForDisconnect()
      {
    // does nothing
      }
   }
