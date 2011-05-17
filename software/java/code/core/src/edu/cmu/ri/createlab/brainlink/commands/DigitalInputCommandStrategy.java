package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialPortCommandResponse;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * Created by IntelliJ IDEA.
 * User: tlauwers
 * Date: May 5, 2011
 */
public class DigitalInputCommandStrategy extends CreateLabSerialDeviceReturnValueCommandStrategy<int[]>
   {
   /** The command character used to request the digital input values. */
   private static final byte COMMAND_PREFIX = '<';
   private final byte[] command;

   /**
    * The size of the expected response, in bytes.
    */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 1;

   public DigitalInputCommandStrategy(final byte whichIO)
      {
      this.command = new byte[]{COMMAND_PREFIX, whichIO};
      }

   protected int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   protected byte[] getCommand()
      {
      return command.clone();
      }

   @Override
   public int[] convertResponse(final SerialPortCommandResponse result)
      {
      if (result != null && result.wasSuccessful())
         {
         final byte[] responseData = result.getData();

         if (responseData != null && responseData.length == SIZE_IN_BYTES_OF_EXPECTED_RESPONSE)
            {
            return new int[]{ByteUtils.unsignedByteToInt(responseData[0])};
            }
         }

      return null;
      }
   }
