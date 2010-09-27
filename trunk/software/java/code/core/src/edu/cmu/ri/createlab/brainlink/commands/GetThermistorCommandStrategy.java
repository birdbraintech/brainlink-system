package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.SerialPortCommandResponse;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class GetThermistorCommandStrategy extends ReturnValueCommandStrategy<Integer>
   {
   /** The command character used to request the value of the thermistor. */
   private static final byte[] COMMAND = {'T'};

   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 2;

   protected int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   protected byte[] getCommand()
      {
      return COMMAND.clone();
      }

   public Integer convertResult(final SerialPortCommandResponse result)
      {
      if (result != null && result.wasSuccessful())
         {
         return ByteUtils.unsignedByteToInt(result.getData()[0]);
         }
      return null;
      }
   }
