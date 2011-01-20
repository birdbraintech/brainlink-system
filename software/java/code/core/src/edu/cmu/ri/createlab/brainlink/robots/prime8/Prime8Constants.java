package edu.cmu.ri.createlab.brainlink.robots.prime8;

import edu.cmu.ri.createlab.brainlink.commands.IRCommandStrategy;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 * @author Tom Lauwers (tlauwers@birdbraintechnologies.com)
 */
final class Prime8Constants
   {
   private static final byte[] INITIALIZATION_COMMAND = new byte[]{0x03, 0x49, 0x01, 0x0D, ByteUtils.intToUnsignedByte(0xD6), 0x02, 0x0C, 0x03, ByteUtils.intToUnsignedByte(0x5C), 0x01, ByteUtils.intToUnsignedByte(0xA0), 0x01, ByteUtils.intToUnsignedByte(0xA0)};

   static byte[] getInitializationCommand()
      {
      return INITIALIZATION_COMMAND.clone();
      }

   private static final class Commands
      {
      private static final byte[] WALK_FORWARD = new byte[]{0x18, 0x40};
      private static final byte[] WALK_BACKWARD = new byte[]{0x18, 0x50};
      private static final byte[] STOP = new byte[]{0x18, ByteUtils.intToUnsignedByte(0x80)};
      private static final byte[] TURN_LEFT = new byte[]{0x18, 0x60};
      private static final byte[] TURN_RIGHT = new byte[]{0x18, 0x70};
      private static final byte[] STAND_UP = new byte[]{0x18, ByteUtils.intToUnsignedByte(0x90)};
      private static final byte[] SIT_DOWN = new byte[]{0x18, ByteUtils.intToUnsignedByte(0xA0)};
      private static final byte[] GROWL = new byte[]{0x18, ByteUtils.intToUnsignedByte(0xC0)};
      private static final byte[] SHOOT = new byte[]{0x18, ByteUtils.intToUnsignedByte(0xB0)};
      private static final byte[] GOOD_GORILLA = new byte[]{0x19, 0x30};
      private static final byte[] GUARD = new byte[]{0x18, ByteUtils.intToUnsignedByte(0xF0)};
      private static final byte[] DEMO = new byte[]{0x1A, 0x60};
      private static final byte[] AUTONOMOUS = new byte[]{0x19, 0x00};
      private static final byte[] ANIMATION = new byte[]{0x19, 0x50};

      private Commands()
         {
         // private to prevent instantiation
         }
      }

   static final class CommandStrategies
      {
      static final IRCommandStrategy WALK_FORWARD_COMMAND_STRATEGY = new IRCommandStrategy(Commands.WALK_FORWARD, (byte)0xC3, (byte)0x50);
      static final IRCommandStrategy WALK_BACKWARD_COMMAND_STRATEGY = new IRCommandStrategy(Commands.WALK_BACKWARD, (byte)0xC3, (byte)0x50);
      static final IRCommandStrategy STOP_COMMAND_STRATEGY = new IRCommandStrategy(Commands.STOP,(byte)0x00, (byte)0x00);
      static final IRCommandStrategy TURN_LEFT_COMMAND_STRATEGY = new IRCommandStrategy(Commands.TURN_LEFT, (byte)0xC3, (byte)0x50);
      static final IRCommandStrategy TURN_RIGHT_COMMAND_STRATEGY = new IRCommandStrategy(Commands.TURN_RIGHT,(byte)0xC3, (byte)0x50);
      static final IRCommandStrategy STAND_UP_COMMAND_STRATEGY = new IRCommandStrategy(Commands.STAND_UP, (byte)0xC3, (byte)0x50);
      static final IRCommandStrategy SIT_DOWN_COMMAND_STRATEGY = new IRCommandStrategy(Commands.SIT_DOWN, (byte)0xC3, (byte)0x50);
      static final IRCommandStrategy GROWL_COMMAND_STRATEGY = new IRCommandStrategy(Commands.GROWL, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy SHOOT_COMMAND_STRATEGY = new IRCommandStrategy(Commands.SHOOT, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy GOOD_GORILLA_COMMAND_STRATEGY = new IRCommandStrategy(Commands.GOOD_GORILLA, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy GUARD_COMMAND_STRATEGY = new IRCommandStrategy(Commands.GUARD, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy DEMO_COMMAND_STRATEGY = new IRCommandStrategy(Commands.DEMO, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy AUTONOMOUS_COMMAND_STRATEGY = new IRCommandStrategy(Commands.AUTONOMOUS, (byte)0x00, (byte)0x00);
      static final IRCommandStrategy ANIMATION_COMMAND_STRATEGY = new IRCommandStrategy(Commands.ANIMATION, (byte)0x00, (byte)0x00);

      private CommandStrategies()
         {
         // private to prevent instantiation
         }
      }

   private Prime8Constants()
      {
      // private to prevent instantiation
      }
   }
