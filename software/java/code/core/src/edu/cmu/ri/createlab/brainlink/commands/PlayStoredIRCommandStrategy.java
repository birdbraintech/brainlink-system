package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * Created by IntelliJ IDEA.
 * User: tlauwers
 * Date: May 5, 2011
 */
public class PlayStoredIRCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to play a stored IR command. */
   private static final byte COMMAND_PREFIX = 'G';

   private final byte[] command;

   public PlayStoredIRCommandStrategy(final int whichSignal, final int repeatTime)
      {
      this.command = new byte[]{COMMAND_PREFIX,
                                (byte)(whichSignal+48),
                                getHighByteFromInt(repeatTime), getLowByteFromInt(repeatTime)};
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
   }