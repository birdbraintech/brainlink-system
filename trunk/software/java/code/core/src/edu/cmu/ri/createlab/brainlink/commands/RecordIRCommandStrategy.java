package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceVariableLengthReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandResponse;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * Created by IntelliJ IDEA.
 * User: tlauwers
 * Date: May 5, 2011
 */
public class RecordIRCommandStrategy extends CreateLabSerialDeviceVariableLengthReturnValueCommandStrategy<int[]>
   {
   /** The command character used to record an IR signal and print that signal over bluetooth. */
   private static final byte[] COMMAND = {'R'};

   protected byte[] getCommand()
      {
      return COMMAND.clone();
      }

   @Override
   protected int getSizeOfExpectedResponseHeader()
      {
      return 1;
      }

   @Override
   protected int getSizeOfVariableLengthResponse(final byte[] headerData)
      {
      return (ByteUtils.unsignedByteToInt(headerData[0]) - 1);
      }

   @Override
   public int[] convertResponse(final SerialDeviceCommandResponse result)
      {
      if (result != null && result.wasSuccessful())
         {
         final byte[] responseData = result.getData();

         if (responseData != null)
            {
            final int numValues = getSizeOfVariableLengthResponse(responseData) / 2;
            final int[] signalTimesInMS = new int[numValues];
            int j = 0;
            // Assumes the first byte holds the length of the response
            for (int i = 1; i < getSizeOfVariableLengthResponse(responseData)+1; i += 2)
               {
               signalTimesInMS[j] = (ByteUtils.unsignedByteToInt(responseData[i]) *256 + ByteUtils.unsignedByteToInt(responseData[i + 1])) * 2;
                   j++;
               }
            return signalTimesInMS;
            }
         }

      return null;
      }
   }

