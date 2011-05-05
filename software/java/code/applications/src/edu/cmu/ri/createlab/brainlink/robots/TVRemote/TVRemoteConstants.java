package edu.cmu.ri.createlab.brainlink.robots.TVRemote;

import edu.cmu.ri.createlab.brainlink.commands.IRCommandStrategy;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Tom Lauwers (tlauwers@birdbraintechnologies.com)
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class TVRemoteConstants
   {
   private static final byte[] INITIALIZATION_COMMAND = new byte[]{0x49, 0x03, 0x49, 0x03, 0x07, 0x08, 0x07, 0x08, 0x01, ByteUtils.intToUnsignedByte(0xD6), 0x02, 0x18, 0x04, ByteUtils.intToUnsignedByte(0xE2), 0x01, ByteUtils.intToUnsignedByte(0xD6), 0x01, ByteUtils.intToUnsignedByte(0xD6)};

   static byte[] getInitializationCommand()
      {
      return INITIALIZATION_COMMAND.clone();
      }

   private static final class Commands
      {
      private static final byte[] POWER = new byte[]{0x14, 0x1E, ByteUtils.intToUnsignedByte(0xBE)};
      private static final byte[] ZERO = new byte[]{0x16, 0x5E, ByteUtils.intToUnsignedByte(0x9A)};
      private static final byte[] ONE = new byte[]{0x17, ByteUtils.intToUnsignedByte(0xEE), ByteUtils.intToUnsignedByte(0x81)};
      private static final byte[] TWO = new byte[]{0x16, 0x1E, ByteUtils.intToUnsignedByte(0x9E)};
      private static final byte[] THREE = new byte[]{0x15, 0x1E, ByteUtils.intToUnsignedByte(0xAE)};
      private static final byte[] FOUR = new byte[]{0x17, 0x1E, ByteUtils.intToUnsignedByte(0x8E)};    
      private static final byte[] FIVE = new byte[]{0x14, ByteUtils.intToUnsignedByte(0x9E), ByteUtils.intToUnsignedByte(0xB6)};
      private static final byte[] SIX = new byte[]{0x16, ByteUtils.intToUnsignedByte(0x9E), ByteUtils.intToUnsignedByte(0x96)};
      private static final byte[] SEVEN = new byte[]{0x15, ByteUtils.intToUnsignedByte(0x9E), ByteUtils.intToUnsignedByte(0xA6)};
      private static final byte[] EIGHT = new byte[]{0x17, ByteUtils.intToUnsignedByte(0x9E), ByteUtils.intToUnsignedByte(0x86)};
      private static final byte[] NINE = new byte[]{0x14, 0x5E, ByteUtils.intToUnsignedByte(0xBA)};     
      private static final byte[] CHUP = new byte[]{0x14, 0x2E, ByteUtils.intToUnsignedByte(0xBD)};
      private static final byte[] CHDOWN = new byte[]{0x14, 0x4E, ByteUtils.intToUnsignedByte(0xBB)};
      private static final byte[] VOLUP = new byte[]{0x14, 0x3E, ByteUtils.intToUnsignedByte(0xBC)};
      private static final byte[] VOLDOWN = new byte[]{0x16, 0x3E, ByteUtils.intToUnsignedByte(0x9C)}; 
      private static final byte[] PLUS100 = new byte[]{0x36, ByteUtils.intToUnsignedByte(0xFC), ByteUtils.intToUnsignedByte(0x90)};
      private static final byte[] DISPLAY = new byte[]{0x15, ByteUtils.intToUnsignedByte(0x8E), ByteUtils.intToUnsignedByte(0xA7)};
      private static final byte[] MENU = new byte[]{0x16, ByteUtils.intToUnsignedByte(0xAE), ByteUtils.intToUnsignedByte(0x95)};
      private static final byte[] MUTE = new byte[]{0x17, 0x3E, ByteUtils.intToUnsignedByte(0x8C)};
      private static final byte[] PLAY = new byte[]{0x15, 0x4E, ByteUtils.intToUnsignedByte(0xAB)}; 
      private static final byte[] STOPVCR = new byte[]{0x14, ByteUtils.intToUnsignedByte(0xEE), ByteUtils.intToUnsignedByte(0xB1)};
      private static final byte[] REW = new byte[]{0x14, ByteUtils.intToUnsignedByte(0xCE), ByteUtils.intToUnsignedByte(0xB3)};
      private static final byte[] FFD = new byte[]{0x14, ByteUtils.intToUnsignedByte(0x8E), ByteUtils.intToUnsignedByte(0xB7)};
      private static final byte[] GAME = new byte[]{ByteUtils.intToUnsignedByte(0x96), ByteUtils.intToUnsignedByte(0xB6), ByteUtils.intToUnsignedByte(0x94)};
      private static final byte[] TPROG = new byte[]{0x17, ByteUtils.intToUnsignedByte(0xCE), ByteUtils.intToUnsignedByte(0x83)};

      private Commands()
         {
         // private to prevent instantiation
         }
      }

   static final class CommandStrategies
      {
      static final IRCommandStrategy POWER_COMMAND_STRATEGY = new IRCommandStrategy(Commands.POWER, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy ZERO_COMMAND_STRATEGY = new IRCommandStrategy(Commands.ZERO, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy ONE_COMMAND_STRATEGY = new IRCommandStrategy(Commands.ONE, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy TWO_COMMAND_STRATEGY = new IRCommandStrategy(Commands.TWO, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy THREE_COMMAND_STRATEGY = new IRCommandStrategy(Commands.THREE, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy FOUR_COMMAND_STRATEGY = new IRCommandStrategy(Commands.FOUR, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy FIVE_COMMAND_STRATEGY = new IRCommandStrategy(Commands.FIVE, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy SIX_COMMAND_STRATEGY = new IRCommandStrategy(Commands.SIX, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy SEVEN_COMMAND_STRATEGY = new IRCommandStrategy(Commands.SEVEN, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy EIGHT_COMMAND_STRATEGY = new IRCommandStrategy(Commands.EIGHT, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy NINE_COMMAND_STRATEGY = new IRCommandStrategy(Commands.NINE, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy CHUP_COMMAND_STRATEGY = new IRCommandStrategy(Commands.CHUP, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy CHDOWN_COMMAND_STRATEGY = new IRCommandStrategy(Commands.CHDOWN, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy VOLUP_COMMAND_STRATEGY = new IRCommandStrategy(Commands.VOLUP, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy VOLDOWN_COMMAND_STRATEGY = new IRCommandStrategy(Commands.VOLDOWN, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy PLUS100_COMMAND_STRATEGY = new IRCommandStrategy(Commands.PLUS100, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy DISPLAY_COMMAND_STRATEGY = new IRCommandStrategy(Commands.DISPLAY, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy MENU_COMMAND_STRATEGY = new IRCommandStrategy(Commands.MENU, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy MUTE_COMMAND_STRATEGY = new IRCommandStrategy(Commands.MUTE, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy PLAY_COMMAND_STRATEGY = new IRCommandStrategy(Commands.PLAY, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy STOPVCR_COMMAND_STRATEGY = new IRCommandStrategy(Commands.STOPVCR, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy REW_COMMAND_STRATEGY = new IRCommandStrategy(Commands.REW, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy FFD_COMMAND_STRATEGY = new IRCommandStrategy(Commands.FFD, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy GAME_COMMAND_STRATEGY = new IRCommandStrategy(Commands.GAME, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy TPROG_COMMAND_STRATEGY = new IRCommandStrategy(Commands.TPROG, (byte)0x00, (byte)0x00);

      private CommandStrategies()
         {
         // private to prevent instantiation
         }
      }

   private TVRemoteConstants()
      {
      // private to prevent instantiation
      }
   }
