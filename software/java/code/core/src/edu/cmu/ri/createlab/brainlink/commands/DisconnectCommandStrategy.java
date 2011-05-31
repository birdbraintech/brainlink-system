package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DisconnectCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The pattern of characters to disconnect from the brainlink and put it back into startup mode. */
   private static final byte[] COMMAND = {'Q'};

   @Override
   protected byte[] getCommand()
      {
      return COMMAND.clone();
      }
   }
