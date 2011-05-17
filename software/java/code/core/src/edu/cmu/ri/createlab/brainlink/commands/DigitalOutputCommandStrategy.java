package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * Created by IntelliJ IDEA.
 * User: tlauwers
 * Date: May 5, 2011
 */
public class DigitalOutputCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to set a digital output. */
   private static final byte COMMAND_PREFIX = '>';

   private final byte[] command;

   public DigitalOutputCommandStrategy(final byte whichOutput, final byte OutputValue)
      {
      this.command = new byte[]{COMMAND_PREFIX,
                                whichOutput,
                                OutputValue};
      }

   protected byte[] getCommand()
      {
      return command.clone();
      }
   }
