package edu.cmu.ri.createlab.brainlink;

import edu.cmu.ri.createlab.device.CreateLabDeviceProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface BrainLink extends CreateLabDeviceProxy
   {
   Integer getBatteryVoltage();

   boolean setFullColorLED(int red, int green, int blue);

   int[] getPhotoresistors();

   int[] getAccelerometerState();

   int[] getAnalogInputs();

   Integer getThermistor();

   boolean playTone(final int frequency);

   boolean turnOffSpeaker();
   }