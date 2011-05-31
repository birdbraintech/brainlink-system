package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceVariableLengthReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandResponse;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * Created by IntelliJ IDEA.
 * User: tlauwers
 * Date: May 5, 2011
 */
public final class AuxSerialReceiveCommandStrategy extends CreateLabSerialDeviceVariableLengthReturnValueCommandStrategy<int[]>
   {
   /** The command character used to request the serial buffer of the auxiliary serial port. */
   private static final byte[] COMMAND = {'r'};

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
            final int sizeOfResponse = getSizeOfVariableLengthResponse(responseData);
            final int[] serialBuffer = new int[sizeOfResponse];
            // Assumes the first byte holds the length of the response
            for (int i = 1; i < responseData[0]; i++)
               {
               serialBuffer[i - 1] = (ByteUtils.unsignedByteToInt(responseData[i]));
               }
            return serialBuffer;
            }
         }

      return null;
      }
   }
