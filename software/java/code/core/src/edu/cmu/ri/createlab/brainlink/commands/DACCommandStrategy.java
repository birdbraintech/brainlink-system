package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * Created by IntelliJ IDEA.
 * User: tlauwers
 * Date: May 5, 2011
 */
public class DACCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to send a DAC command. */
   private static final byte COMMAND_PREFIX = 'd';

   private final byte[] command;

   public DACCommandStrategy(final int whichDAC, final int dacValue)
      {
          byte trueDacValue = (byte)(dacValue*255/3300); // Convert from millivolts to 8-bit range
      this.command = new byte[]{COMMAND_PREFIX,
                                (byte)(whichDAC+48),
                                trueDacValue};
      }

   @Override
   protected byte[] getCommand()
      {
      return command.clone();
      }
   }
