package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * Created by IntelliJ IDEA.
 * User: tlauwers
 * Date: May 5, 2011
 */
public class PWMCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to set one of Brainlink's PWM ports. */
   private static final byte COMMAND_PREFIX = 'p';

   private final byte[] command;

   public PWMCommandStrategy(final byte whichPWM, final byte pwmDuty)
      {
      this.command = new byte[]{COMMAND_PREFIX,
                                whichPWM,
                                getHighByteFromInt(pwmDuty),
                                getLowByteFromInt(pwmDuty)};
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
