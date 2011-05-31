package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class InitializeIRCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to initialize the IR. */
   private static final byte COMMAND_PREFIX = 'I';

   private final byte[] command;

   public InitializeIRCommandStrategy(final byte[] initializationBytes)
      {
      this.command = new byte[initializationBytes.length + 1];
      this.command[0] = COMMAND_PREFIX;
      System.arraycopy(initializationBytes, 0, this.command, 1, initializationBytes.length);
      }

   @Override
   protected byte[] getCommand()
      {
      return command.clone();
      }
   }