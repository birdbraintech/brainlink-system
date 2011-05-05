package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.SerialPortCommandResponse;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * Created by IntelliJ IDEA.
 * User: tlauwers
 * Date: May 5, 2011
 */
public class PrintStoredIRCommandStrategy extends ReturnValueCommandStrategy<int[]>
{
 /** The command character used to print over bluetooth a stored IR signal. */
   private static final byte COMMAND_PREFIX = 'r';
   private final byte[] command;

    public PrintStoredIRCommandStrategy(final byte whichSignal)
      {
           this.command = new byte[]{COMMAND_PREFIX, whichSignal};
      }


   protected byte[] getCommand()
      {
      return command.clone();
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
                size_of_response = (responseData[0]-1)/2;
                int [] signalTimesInMS = new int[size_of_response];
                int j= 0;
                // Assumes the first byte holds the length of the response
                for(int i = 1; i < responseData[0]; i+=2)
                {
                    signalTimesInMS[j] = (ByteUtils.unsignedByteToInt(responseData[i])<<8 + ByteUtils.unsignedByteToInt(responseData[i+1]))*2;
                    j++;
                }
                return signalTimesInMS;
            }
         }

      return null;
      }

}
