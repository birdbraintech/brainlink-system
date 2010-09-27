package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.SerialPortCommandResponse;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class GetAccelerometerCommandStrategy extends ReturnValueCommandStrategy<int[]>
   {
   /** The command character used to request the value of the accelerometer. */
   private static final byte[] COMMAND = {'A'};

   /**
    * The size of the expected response, in bytes.  The first three are the X, Y, and Z values and the 4th byte is the
    * tap/shake byte.
    */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 4;

   protected int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   protected byte[] getCommand()
      {
      return COMMAND.clone();
      }

   @Override
   public int[] convertResult(final SerialPortCommandResponse result)
      {
      if (result != null && result.wasSuccessful())
         {
         final byte[] responseData = result.getData();

         if (responseData != null && responseData.length == SIZE_IN_BYTES_OF_EXPECTED_RESPONSE)
            {
            return new int[]{ByteUtils.unsignedByteToInt(responseData[0]),
                             ByteUtils.unsignedByteToInt(responseData[1]),
                             ByteUtils.unsignedByteToInt(responseData[2]),
                             ByteUtils.unsignedByteToInt(responseData[3])};
            }
         }

      return null;
      }
   }
