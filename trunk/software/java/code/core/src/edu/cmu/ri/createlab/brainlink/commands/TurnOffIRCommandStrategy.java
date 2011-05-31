package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class TurnOffIRCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to turn off IR. */
   private static final byte[] COMMAND = {'!'};

   @Override
   protected byte[] getCommand()
      {
      return COMMAND.clone();
      }
   }
