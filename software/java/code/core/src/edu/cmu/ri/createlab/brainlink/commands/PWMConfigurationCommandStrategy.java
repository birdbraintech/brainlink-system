package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * Created by IntelliJ IDEA.
 * User: tlauwers
 * Date: May 5, 2011
 * No longer needed
 */
public class PWMConfigurationCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to set up the PWM carrier frequency. */
   private static final byte COMMAND_PREFIX = 'P';

   private final byte[] command;

   public PWMConfigurationCommandStrategy(final byte pwmFrequency)
      {
      this.command = new byte[]{COMMAND_PREFIX,
                                getHighByteFromInt(pwmFrequency),
                                getLowByteFromInt(pwmFrequency)};
      }

   private byte getHighByteFromInt(final int val)
      {
      return (byte)((val << 16) >> 24);
      }

   private byte getLowByteFromInt(final int val)
      {
      return (byte)((val << 24) >> 24);
      }

   protected byte[] getCommand()
      {
      return command.clone();
      }
   }
