package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.SerialPortCommandResponse;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * Created by IntelliJ IDEA.
 * User: tlauwers
 * Date: May 5, 2011
 */
public final class AuxSerialReceiveCommandStrategy extends ReturnValueCommandStrategy<int[]>
{
 /** The command character used to request the serial buffer of the auxiliary serial port. */
   private static final byte[] COMMAND = {'r'};


   protected byte[] getCommand()
      {
      return COMMAND.clone();
      }
    /*
      * This may do the right thing, or maybe not...You kind of have to do convertResult before you can call getSizeOfExpectedResponse
      */
    int size_of_response = 0;
    protected int getSizeOfExpectedResponse()
       {
       return size_of_response;
       }

    @Override
    public int[] convertResult(final SerialPortCommandResponse result)
       {
       if (result != null && result.wasSuccessful())
          {
              final byte[] responseData = result.getData();

          if (responseData != null)
             {
                 size_of_response = (responseData[0]-1);
                 int [] serialBuffer = new int[size_of_response];
                 // Assumes the first byte holds the length of the response
                 for(int i = 1; i < responseData[0]; i++)
                 {
                     serialBuffer[i-1] = (ByteUtils.unsignedByteToInt(responseData[i]));
                 }
                 return serialBuffer;
             }
          }

       return null;
       }


}
