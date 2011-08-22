package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * Created by IntelliJ IDEA.
 * User: tlauwers
 * Date: May 5, 2011
 */
public class StoreIRCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to indicate where the most recently captured IR signal should be stored.*/
   private static final byte COMMAND_PREFIX = 'S';

   private final byte[] command;

   public StoreIRCommandStrategy(final int whichSignal)
      {
      this.command = new byte[]{COMMAND_PREFIX,
                                (byte)(whichSignal+48)};
      }

   @Override
   protected byte[] getCommand()
      {
      return command.clone();
      }
   }
