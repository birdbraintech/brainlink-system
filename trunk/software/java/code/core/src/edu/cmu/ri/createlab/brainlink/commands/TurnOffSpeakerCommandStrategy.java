package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class TurnOffSpeakerCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to turn off the speaker. */
   private static final byte[] COMMAND = {'b'};

   @Override
   protected byte[] getCommand()
      {
      return COMMAND.clone();
      }
   }
