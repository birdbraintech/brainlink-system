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
   private static final byte COMMAND_PREFIX = 'P';

   private final byte[] command;

   public PWMCommandStrategy(final int whichPWM, final int pwmDuty, final int pwmFrequency)
      {

         int pwmPER = 32000000/pwmFrequency - 1;   // Calculate the Period based on desired frequency
         int trueDuty = pwmPER*pwmDuty/1000;       // Calculate the real duty cycle based on desired duty cycle                                                   
      this.command = new byte[]{COMMAND_PREFIX,
                                getHighByteFromInt(pwmPER),
                                getLowByteFromInt(pwmPER),
                                'p',
                                (byte)(whichPWM+48),
                                getHighByteFromInt(trueDuty),
                                getLowByteFromInt(trueDuty)};
      }

   private byte getHighByteFromInt(final int val)
      {
      return (byte)((val << 16) >> 24);
      }

   private byte getLowByteFromInt(final int val)
      {
      return (byte)((val << 24) >> 24);
      }

   @Override
   protected byte[] getCommand()
      {
      return command.clone();
      }
   }
