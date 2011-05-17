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
      this.command = new byte[bytesToTransmit.length + 1];
      this.command[0] = COMMAND_PREFIX;
      System.arraycopy(bytesToTransmit, 0, this.command, 1, bytesToTransmit.length);
      }

   protected byte[] getCommand()
      {
      return command.clone();
      }
   }
