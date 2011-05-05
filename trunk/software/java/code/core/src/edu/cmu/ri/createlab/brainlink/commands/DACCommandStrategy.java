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

   public DACCommandStrategy(final byte whichDAC, final byte DACValue)
      {
      this.command = new byte[]{COMMAND_PREFIX,
                                whichDAC,
                                DACValue};
      }

   protected byte[] getCommand()
      {
      return command.clone();
      }
}
