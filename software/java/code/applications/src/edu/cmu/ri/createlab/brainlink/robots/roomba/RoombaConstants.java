package edu.cmu.ri.createlab.brainlink.robots.roomba;

import edu.cmu.ri.createlab.brainlink.commands.IRCommandStrategy;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Tom Lauwers (tlauwers@birdbraintechnologies.com)
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class RoombaConstants
   {
   private static final byte[] INITIALIZATION_COMMAND = new byte[]{0x03, 0x49, 0x00, 0x03, 0x08, 0x05, ByteUtils.intToUnsignedByte(0xDC), 0x01, ByteUtils.intToUnsignedByte(0xF4), 0x00, 0x00};

   static byte[] getInitializationCommand()
      {
      return INITIALIZATION_COMMAND.clone();
      }

   private static final class Commands
      {

      private static final byte[] DRIVE_FORWARD = new byte[]{ByteUtils.intToUnsignedByte(0x82)};
      private static final byte[] TURN_LEFT = new byte[]{ByteUtils.intToUnsignedByte(0x81)};
      private static final byte[] TURN_RIGHT = new byte[]{ByteUtils.intToUnsignedByte(0x83)};
      private static final byte[] CLEAN = new byte[]{ByteUtils.intToUnsignedByte(0x88)};
      private static final byte[] MAX = new byte[]{ByteUtils.intToUnsignedByte(0x85)};
      private static final byte[] STOP = new byte[]{ByteUtils.intToUnsignedByte(0x89)};
      private static final byte[] POWER_OFF = new byte[]{ByteUtils.intToUnsignedByte(0x8A)};
      private static final byte[] SPOT = new byte[]{ByteUtils.intToUnsignedByte(0x84)};
      private static final byte[] DOCK = new byte[]{ByteUtils.intToUnsignedByte(0xF0)};          // UNTESTED

      private Commands()
         {
         // private to prevent instantiation
         }
      }

   static final class CommandStrategies
      {
      static final IRCommandStrategy DRIVE_FORWARD_COMMAND_STRATEGY = new IRCommandStrategy(Commands.DRIVE_FORWARD, (byte)0x27, (byte)0x10);
      static final IRCommandStrategy STOP_COMMAND_STRATEGY = new IRCommandStrategy(Commands.STOP, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy TURN_LEFT_COMMAND_STRATEGY = new IRCommandStrategy(Commands.TURN_LEFT, (byte)0x27, (byte)0x10);
      static final IRCommandStrategy TURN_RIGHT_COMMAND_STRATEGY = new IRCommandStrategy(Commands.TURN_RIGHT, (byte)0x27, (byte)0x10);
      static final IRCommandStrategy CLEAN_COMMAND_STRATEGY = new IRCommandStrategy(Commands.CLEAN, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy MAX_COMMAND_STRATEGY = new IRCommandStrategy(Commands.MAX, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy POWER_OFF_COMMAND_STRATEGY = new IRCommandStrategy(Commands.POWER_OFF, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy SPOT_COMMAND_STRATEGY = new IRCommandStrategy(Commands.SPOT, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy DOCK_COMMAND_STRATEGY = new IRCommandStrategy(Commands.DOCK, (byte)0x00, (byte)0x00);

      private CommandStrategies()
         {
         // private to prevent instantiation
         }
      }

   private RoombaConstants()
      {
      // private to prevent instantiation
      }
   }
