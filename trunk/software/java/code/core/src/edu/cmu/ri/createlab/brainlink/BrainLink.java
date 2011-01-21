package edu.cmu.ri.createlab.brainlink;

import java.awt.Color;
import edu.cmu.ri.createlab.brainlink.commands.IRCommandStrategy;
import edu.cmu.ri.createlab.brainlink.commands.SimpleIRCommandStrategy;
import edu.cmu.ri.createlab.device.CreateLabDeviceProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface BrainLink extends CreateLabDeviceProxy
   {
   /** Returns the current battery voltage; returns <code>null</code> if the voltage could not be read. */
   Integer getBatteryVoltage();

   /** Sets the full color LED to the given color specified by the red, green, and blue values. */
   boolean setFullColorLED(final int red, final int green, final int blue);

   /** Sets the full color LED to the given {@link Color}. */
   boolean setFullColorLED(final Color color);

   /** Returns the light sensor values; returns <code>null</code> if the light sensors could not be read. */
   int[] getLightSensors();

   /** Returns the accelerometer values in Gs; returns <code>null</code> if the accelerometer could not be read. */
   double[] getAccelerometerValuesInGs();

   /**
    * Returns the value of the accelerometer's X axis in Gs; returns <code>null</code> if the accelerometer could not be
    * read.
    */
   Double getXAccelerometer();

   /**
    * Returns the value of the accelerometer's Y axis in Gs; returns <code>null</code> if the accelerometer could not be
    * read.
    */
   Double getYAccelerometer();

   /**
    * Returns the value of the accelerometer's Z axis in Gs; returns <code>null</code> if the accelerometer could not be
    * read.
    */
   Double getZAccelerometer();

   /**
    * Returns <code>true</code> if the BrainLink has been shaken since the last accelerometer read, <code>false</code>
    * otherise.  Returns <code>null</code> if the accelerometer could not be read.
    */
   Boolean wasShaken();

   /**
    * Returns <code>true</code> if the BrainLink has been tapped since the last accelerometer read, <code>false</code>
    * otherise.  Returns <code>null</code> if the accelerometer could not be read.
    */
   Boolean wasTapped();

   /**
    * Returns <code>true</code> if the BrainLink has been shaken or tapped since the last accelerometer read,
    * <code>false</code> otherise.  Returns <code>null</code> if the accelerometer could not be read.
    */
   Boolean wasShakenOTapped();

   /** Returns the analog input values; returns <code>null</code> if the inputs could not be read. */
   int[] getAnalogInputs();

   /**
    * Returns the value of the given analog input; returns <code>null</code> if the specified port could not be read or
    * is invald.
    */
   Integer getAnalogInput(int port);

   /** Returns the value of the left light sensor; returns <code>null</code> if the light sensor could not be read. */
   Integer getLeftLightSensor();

   /** Returns the value of the right light sensor; returns <code>null</code> if the light sensor could not be read. */
   Integer getRightLightSensor();

   /** Returns the thermistor value; returns <code>null</code> if the thermistor could not be read. */
   Integer getThermistor();

   boolean playTone(final int frequency);

   boolean turnOffSpeaker();

   boolean initializeIR(final byte[] initializationBytes);

   boolean sendSimpleIRCommand(final SimpleIRCommandStrategy commandStrategy);

   boolean sendSimpleIRCommand(final byte command);

   boolean turnOffIR();

   boolean sendIRCommand(final IRCommandStrategy commandStrategy);
   }