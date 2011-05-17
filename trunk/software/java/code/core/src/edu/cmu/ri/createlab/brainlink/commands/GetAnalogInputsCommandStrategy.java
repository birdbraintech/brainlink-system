package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialPortCommandResponse;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetAnalogInputsCommandStrategy extends CreateLabSerialDeviceReturnValueCommandStrategy<int[]>
   {
   /** The command character used to request the analog input values. */
   private static final byte[] COMMAND = {'X'};

   /**
    * The size of the expected response, in bytes.
    */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 6;

   protected int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   protected byte[] getCommand()
      {
      return COMMAND.clone();
      }

   @Override
   public int[] convertResponse(final SerialPortCommandResponse result)
      {
      if (result != null && result.wasSuccessful())
         {
         final byte[] responseData = result.getData();

         if (responseData != null && responseData.length == SIZE_IN_BYTES_OF_EXPECTED_RESPONSE)
            {
            return new int[]{ByteUtils.unsignedByteToInt(responseData[0]),
                             ByteUtils.unsignedByteToInt(responseData[1]),
                             ByteUtils.unsignedByteToInt(responseData[2]),
                             ByteUtils.unsignedByteToInt(responseData[3]),
                             ByteUtils.unsignedByteToInt(responseData[4]),
                             ByteUtils.unsignedByteToInt(responseData[5])};
            }
         }

      return null;
      }
   }
