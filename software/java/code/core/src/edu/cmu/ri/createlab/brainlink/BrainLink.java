package edu.cmu.ri.createlab.brainlink;

import java.awt.Color;
import edu.cmu.ri.createlab.brainlink.commands.SimpleIRCommandStrategy;
import edu.cmu.ri.createlab.device.CreateLabDeviceProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface BrainLink extends CreateLabDeviceProxy
   {
   Integer getBatteryVoltage();

   boolean setFullColorLED(final int red, final int green, final int blue);

   boolean setFullColorLED(final Color color);

   int[] getPhotoresistors();

   int[] getAccelerometerState();

   int[] getAnalogInputs();

   Integer getThermistor();

   boolean playTone(final int frequency);

   boolean turnOffSpeaker();

   boolean initializeIR(final byte[] initializationBytes);

   boolean sendSimpleIRCommand(final SimpleIRCommandStrategy commandStrategy);

   boolean sendSimpleIRCommand(final byte command);

   boolean turnOffIR();
   }