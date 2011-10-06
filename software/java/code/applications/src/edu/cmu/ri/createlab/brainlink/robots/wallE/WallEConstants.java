package edu.cmu.ri.createlab.brainlink.robots.wallE;

import edu.cmu.ri.createlab.brainlink.commands.IRCommandStrategy;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Tom Lauwers (tlauwers@birdbraintechnologies.com)
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class WallEConstants
   {
   private static final byte[] INITIALIZATION_COMMAND = new byte[]{0x03, 0x49, 0x02, 0x0B, 0x6D, 0x03, ByteUtils.intToUnsignedByte(0xB6), 0x03, 0x15, 0x02, ByteUtils.intToUnsignedByte(0xD5), 0x00, ByteUtils.intToUnsignedByte(0xE1), 0x00, 0x00};

   static byte[] getInitializationCommand()
      {
      return INITIALIZATION_COMMAND.clone();
      }

   private static final class Commands
      {
      private static final byte[] DRIVE_FORWARD_ONCE = new byte[]{ByteUtils.intToUnsignedByte(0xAA), ByteUtils.intToUnsignedByte(0xEA), ByteUtils.intToUnsignedByte(0xE8)};
      private static final byte[] DRIVE_FORWARD = new byte[]{ByteUtils.intToUnsignedByte(0xAF), ByteUtils.intToUnsignedByte(0xEF), ByteUtils.intToUnsignedByte(0xE8)};
      private static final byte[] TURN_LEFT = new byte[]{ByteUtils.intToUnsignedByte(0xAF), ByteUtils.intToUnsignedByte(0xDF), ByteUtils.intToUnsignedByte(0xD8)};
      private static final byte[] SMALL_LEFT_TURN = new byte[]{ByteUtils.intToUnsignedByte(0xAA), ByteUtils.intToUnsignedByte(0xCA), ByteUtils.intToUnsignedByte(0xC8)};
      private static final byte[] MEDIUM_LEFT_TURN = new byte[]{ByteUtils.intToUnsignedByte(0xAA), ByteUtils.intToUnsignedByte(0xDA), ByteUtils.intToUnsignedByte(0xD8)};
      private static final byte[] LARGE_LEFT_TURN = new byte[]{ByteUtils.intToUnsignedByte(0xAA), ByteUtils.intToUnsignedByte(0xBA), ByteUtils.intToUnsignedByte(0xB8)};
      private static final byte[] SPECIAL_TURN = new byte[]{ByteUtils.intToUnsignedByte(0xAA), 0x5A, 0x58};
      private static final byte[] MUSIC = new byte[]{ByteUtils.intToUnsignedByte(0xAA), 0x6A, 0x68};
      private static final byte[] SUN = new byte[]{ByteUtils.intToUnsignedByte(0xAA), 0x4A, 0x48};
      private static final byte[] BUTTERFLY = new byte[]{ByteUtils.intToUnsignedByte(0xAA), 0x7A, 0x78};
      private static final byte[] TALK_CLOUD = new byte[]{ByteUtils.intToUnsignedByte(0xAA), ByteUtils.intToUnsignedByte(0xAA), ByteUtils.intToUnsignedByte(0xA8)};
      private static final byte[] GO_STOP = new byte[]{ByteUtils.intToUnsignedByte(0xAD), ByteUtils.intToUnsignedByte(0xFD), ByteUtils.intToUnsignedByte(0xF8)};
      private static final byte[] BOX = new byte[]{ByteUtils.intToUnsignedByte(0xAA), ByteUtils.intToUnsignedByte(0x9A), ByteUtils.intToUnsignedByte(0x98)};
      private static final byte[] PROG = new byte[]{ByteUtils.intToUnsignedByte(0xAC), ByteUtils.intToUnsignedByte(0x8C), ByteUtils.intToUnsignedByte(0x88)};

      private Commands()
         {
         // private to prevent instantiation
         }
      }

   static final class CommandStrategies
      {
      static final IRCommandStrategy DRIVE_FORWARD_COMMAND_STRATEGY = new IRCommandStrategy(Commands.DRIVE_FORWARD, (byte)0xB9, (byte)0x8C);
      static final IRCommandStrategy TURN_LEFT_COMMAND_STRATEGY = new IRCommandStrategy(Commands.TURN_LEFT, (byte)0xB9, (byte)0x8C);
      static final IRCommandStrategy DRIVE_FORWARD_ONCE_COMMAND_STRATEGY = new IRCommandStrategy(Commands.DRIVE_FORWARD_ONCE, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy SMALL_LEFT_TURN_COMMAND_STRATEGY = new IRCommandStrategy(Commands.SMALL_LEFT_TURN, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy MEDIUM_LEFT_TURN_COMMAND_STRATEGY = new IRCommandStrategy(Commands.MEDIUM_LEFT_TURN, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy LARGE_LEFT_TURN_COMMAND_STRATEGY = new IRCommandStrategy(Commands.LARGE_LEFT_TURN, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy SPECIAL_TURN_COMMAND_STRATEGY = new IRCommandStrategy(Commands.SPECIAL_TURN, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy MUSIC_COMMAND_STRATEGY = new IRCommandStrategy(Commands.MUSIC, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy SUN_COMMAND_STRATEGY = new IRCommandStrategy(Commands.SUN, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy BUTTERFLY_COMMAND_STRATEGY = new IRCommandStrategy(Commands.BUTTERFLY, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy TALK_CLOUD_COMMAND_STRATEGY = new IRCommandStrategy(Commands.TALK_CLOUD, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy GO_STOP_COMMAND_STRATEGY = new IRCommandStrategy(Commands.GO_STOP, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy BOX_COMMAND_STRATEGY = new IRCommandStrategy(Commands.BOX, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy PROG_COMMAND_STRATEGY = new IRCommandStrategy(Commands.PROG, (byte)0x00, (byte)0x00);

      private CommandStrategies()
         {
         // private to prevent instantiation
         }
      }

   private WallEConstants()
      {
      // private to prevent instantiation
      }
   }
