package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.brainlink.BrainLinkConstants;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;
import edu.cmu.ri.createlab.util.MathUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class PlayToneCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to set the tone frequency. */
   private static final byte COMMAND_PREFIX = 'D';

   private final byte[] command;

   public PlayToneCommandStrategy(final int frequency)
      {
      final int cleanedFrequency = MathUtils.ensureRange(frequency, BrainLinkConstants.TONE_MIN_FREQUENCY, BrainLinkConstants.TONE_MAX_FREQUENCY);
      this.command = new byte[]{COMMAND_PREFIX,
                                getHighByteFromInt(cleanedFrequency),
                                getLowByteFromInt(cleanedFrequency)};
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
