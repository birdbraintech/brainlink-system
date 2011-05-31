package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * Created by IntelliJ IDEA.
 * User: tlauwers
 * Date: May 5, 2011
 */
public class SendRawIRCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to play a stored IR command. */
   private static final byte COMMAND_PREFIX = 's';

   private final byte[] command;

   public SendRawIRCommandStrategy(final int[] signal, final int repeatTime)
      {
      this.command = new byte[signal.length * 2 + 3];
      this.command[0] = COMMAND_PREFIX;
      int j = 1;
      for (int i = 0; i < signal.length; i++)
         {
         this.command[j] = getHighByteFromInt(signal[i]);
         j++;
         this.command[j] = getLowByteFromInt(signal[i]);
         j++;
         }
      this.command[j] = getHighByteFromInt(repeatTime);
      j++;
      this.command[j] = getLowByteFromInt(repeatTime);
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