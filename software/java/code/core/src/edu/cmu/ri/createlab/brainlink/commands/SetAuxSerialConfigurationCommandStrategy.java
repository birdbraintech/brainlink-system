package edu.cmu.ri.createlab.brainlink.commands;


/**
 * Created by IntelliJ IDEA.
 * User: tlauwers
 * Date: May 5, 2011
 */
public final class SetAuxSerialConfigurationCommandStrategy extends CustomCommandEchoNoReturnValueCommandStrategy
   {
   /** The command character used to send an aux serial config command. */
   private static final byte COMMAND_PREFIX = 'C';

   private final byte[] command;
   private final byte[] expectedCommandEcho;

   public SetAuxSerialConfigurationCommandStrategy(final int baudRate)
      {
      int baud;
      byte scale;
      if (baudRate >= 9600)
         {
         baud = (32000000/baudRate - 16);
         scale = -4;
         this.command = new byte[]{COMMAND_PREFIX,
                                   getHighByteFromInt(baud),
                                   getLowByteFromInt(baud), scale};
         }
      else
         {
         baud = (int)((32000000 / (baudRate * 16) - 1));
         scale = 0;
         this.command = new byte[]{COMMAND_PREFIX,
                                   getHighByteFromInt(baud),
                                   getLowByteFromInt(baud), scale};
         }
       // Fix for the bug in release firmware version 1.0
      this.expectedCommandEcho = new byte[command.length + 1];
      System.arraycopy(command, 0, expectedCommandEcho, 0, command.length);   // make an exact copy
      this.expectedCommandEcho[command.length] = scale; // Brainlink sends the scale byte back twice.


      }

   private byte getHighByteFromInt(final int val)
      {
      return (byte)((val << 16) >> 24);
      }

   private byte getLowByteFromInt(final int val)
      {
      return (byte)((val << 24) >> 24);
      }

   @Override
   protected byte[] getCommand()
      {
      return command.clone();
      }

   @Override
   protected byte[] getExpectedCommandEcho()
      {
      return expectedCommandEcho.clone();
      }
   }
