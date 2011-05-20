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

   public DigitalOutputCommandStrategy(final int whichOutput, final Boolean OutputValue)
      {
          if(OutputValue) {
              this.command = new byte[]{COMMAND_PREFIX,
                                        (byte)whichOutput,
                                        '1'};
          }
          else {
              this.command = new byte[]{COMMAND_PREFIX,
                                        (byte)whichOutput,
                                        '0'};              
          }
      }

   protected byte[] getCommand()
      {
      return command.clone();
      }
   }
