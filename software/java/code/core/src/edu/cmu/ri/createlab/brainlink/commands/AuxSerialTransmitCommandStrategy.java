package edu.cmu.ri.createlab.brainlink.commands;

/**
 * @author Tom Lauwers (tlauwers@birdbraintechnologies.com)
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class AuxSerialTransmitCommandStrategy extends ThrottledNoReturnValueCommandStrategy
   {
   /** The command character used to send an aux serial transmit command. */
   private static final byte COMMAND_PREFIX = 't';

   private final byte[] command;

   public AuxSerialTransmitCommandStrategy(final byte[] bytesToTransmit)
      {
      // pause for 10 milliseconds after every 16 bytes written
      super(10, 16);

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
