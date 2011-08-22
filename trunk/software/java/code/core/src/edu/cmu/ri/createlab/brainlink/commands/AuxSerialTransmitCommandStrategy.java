package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * Created by IntelliJ IDEA.
 * User: tlauwers
 * Date: May 5, 2011
 */
public class AuxSerialTransmitCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to send an aux serial transmit command. */
   private static final byte COMMAND_PREFIX = 't';

   private final byte[] command;

   public AuxSerialTransmitCommandStrategy(final byte[] bytesToTransmit)
      {
      this.command = new byte[bytesToTransmit.length + 2];
      this.command[0] = COMMAND_PREFIX;
      this.command[1] = (byte)bytesToTransmit.length;
      System.arraycopy(bytesToTransmit, 0, this.command, 2, bytesToTransmit.length);
      }

   @Override
   protected byte[] getCommand()
      {
      return command.clone();
      }
   }
