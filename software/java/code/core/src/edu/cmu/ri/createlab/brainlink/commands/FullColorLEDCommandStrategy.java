package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.brainlink.BrainLinkConstants;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;
import edu.cmu.ri.createlab.util.ByteUtils;
import edu.cmu.ri.createlab.util.MathUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FullColorLEDCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to turn on a full-color LED. */
   private static final byte COMMAND_PREFIX = 'O';

   private final byte[] command;

   public FullColorLEDCommandStrategy(final int red, final int green, final int blue)
      {
      this.command = new byte[]{COMMAND_PREFIX,
                                ByteUtils.intToUnsignedByte(MathUtils.ensureRange(red, BrainLinkConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY, BrainLinkConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY)),
                                ByteUtils.intToUnsignedByte(MathUtils.ensureRange(green, BrainLinkConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY, BrainLinkConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY)),
                                ByteUtils.intToUnsignedByte(MathUtils.ensureRange(blue, BrainLinkConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY, BrainLinkConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY))};
      }

   protected byte[] getCommand()
      {
      return command.clone();
      }
   }
