package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandResponse;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetThermistorCommandStrategy extends CreateLabSerialDeviceReturnValueCommandStrategy<Integer>
   {
   /** The command character used to request the value of the thermistor. */
   private static final byte[] COMMAND = {'T'};

   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 1;

   @Override
   protected int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   @Override
   protected byte[] getCommand()
      {
      return COMMAND.clone();
      }

   @Override
   public Integer convertResponse(final SerialDeviceCommandResponse result)
      {
      if (result != null && result.wasSuccessful())
         {
         return ByteUtils.unsignedByteToInt(result.getData()[0]);
         }
      return null;
      }
   }
