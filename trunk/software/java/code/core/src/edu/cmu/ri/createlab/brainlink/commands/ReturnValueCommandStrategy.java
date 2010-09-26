package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialPortCommandResponse;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class ReturnValueCommandStrategy<T> extends CreateLabSerialDeviceReturnValueCommandStrategy
   {
   public abstract T convertResult(final SerialPortCommandResponse result);
   }
