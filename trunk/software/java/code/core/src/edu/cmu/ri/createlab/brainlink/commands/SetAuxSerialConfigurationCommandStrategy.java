package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * Created by IntelliJ IDEA.
 * User: tlauwers
 * Date: May 5, 2011
 */
public final class SetAuxSerialConfigurationCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
    {
   /** The command character used to send an aux serial config command. */
   private static final byte COMMAND_PREFIX = 'C';

   private final byte[] command;

   public SetAuxSerialConfigurationCommandStrategy(final int baudRate)
      {
          if(baudRate >= 9600)
              {
              int baud = (int)((1/((double)baudRate*16/32000000-1))*16);
              byte scale = -4;
               this.command = new byte[]{COMMAND_PREFIX,
                                    getHighByteFromInt(baud),
                                    getLowByteFromInt(baud), scale};
              }
          else
          {
              int baud = (int)((32000000/baudRate*16-1);
                        byte scale = 0;
                         this.command = new byte[]{COMMAND_PREFIX,
                                              getHighByteFromInt(baud),
                                              getLowByteFromInt(baud), scale};

          }
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
