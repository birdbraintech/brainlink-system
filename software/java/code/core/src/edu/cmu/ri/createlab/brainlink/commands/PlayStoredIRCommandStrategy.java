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

   public PlayStoredIRCommandStrategy(final byte whichSignal, final byte repeatCommandByte1, final byte repeatCommandByte2)
      {
      this.command = new byte[]{COMMAND_PREFIX,
                                whichSignal,
                                repeatCommandByte1, repeatCommandByte2};
      }

   protected byte[] getCommand()
      {
      return command.clone();
      }
   }