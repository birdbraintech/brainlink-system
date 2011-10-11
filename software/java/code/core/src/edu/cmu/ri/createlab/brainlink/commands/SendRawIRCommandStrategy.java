package edu.cmu.ri.createlab.brainlink.commands;

/**
 * @author Tom Lauwers (tlauwers@birdbraintechnologies.com)
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class SendRawIRCommandStrategy extends ThrottledNoReturnValueCommandStrategy
   {
   /** The command character used to play a stored IR command. */
   private static final byte COMMAND_PREFIX = 's';

   private final byte[] command;

   public SendRawIRCommandStrategy(final int[] signal, final int repeatTime)
      {
      // pause for 10 milliseconds after every 16 bytes written
      super(10, 16);

      this.command = new byte[signal.length * 2 + 4];
      this.command[0] = COMMAND_PREFIX;
      this.command[1] = getLowByteFromInt((signal.length * 2 + 1));
      int j = 2;
      for (int i = 0; i < signal.length; i++)
         {
         // The signal is in microseconds, but the Brainlink expects it in increments of 2 us, so we divide by 2
         signal[i] /= 2;
         this.command[j] = getHighByteFromInt(signal[i]);
         j++;
         this.command[j] = getLowByteFromInt(signal[i]);
         j++;
         }
      this.command[j] = getHighByteFromInt(repeatTime);
      j++;
      this.command[j] = getLowByteFromInt(repeatTime);
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