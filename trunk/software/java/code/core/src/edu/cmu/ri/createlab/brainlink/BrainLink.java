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
   /** Returns the current battery voltage; returns <code>null</code> if the voltage could not be read.
    *  @return The raw battery voltage reading*/
   Integer getBatteryVoltage();

   /**Sets the color of the LED in the Brainlink.  The LED can be any color that can be
    * created by mixing red, green, and blue; turning on all three colors in equal amounts results
    * in white light.  Valid ranges for the red, green, and blue elements are 0 to 255.
    *
    * @param     red sets the intensity of the red element of the LED
    * @param     green sets the intensity of the green element of the LED
    * @param     blue sets the intensity of the blue element of the LED
    * @return true if LED was successfully set, false otherwise
    */
   boolean setFullColorLED(final int red, final int green, final int blue);

   /** Sets the full color LED to the given {@link Color}.
    *  @param   color sets the color of the LED
    *
    *  @return true if LED was successfully set, false otherwise */
   boolean setFullColorLED(final Color color);

   /** Returns the light sensor values; returns <code>null</code> if the light sensors could not be read.
    * @return a two int array containing the left and right light sensor values. */
   int[] getLightSensors();

   /** Returns the accelerometer values in Gs; returns <code>null</code> if the accelerometer could not be read.
    * @return an array containing the X, Y, and Z accelerometer readings in gees */
   double[] getAccelerometerValuesInGs();

   /**
    * Returns the value of the accelerometer's X axis in Gs; returns <code>null</code> if the accelerometer could not be
    * read.
    * @return the X acceleration in Gs
    */
   Double getXAccelerometer();

   /**
    * Returns the value of the accelerometer's Y axis in Gs; returns <code>null</code> if the accelerometer could not be
    * read.
    * @return the Y Acceleration in Gs
    */
   Double getYAccelerometer();

   /**
    * Returns the value of the accelerometer's Z axis in Gs; returns <code>null</code> if the accelerometer could not be
    * read.
    * @return the Z acceleration in Gs
    */
   Double getZAccelerometer();

   /**
    * Returns <code>true</code> if the BrainLink has been shaken since the last accelerometer read, <code>false</code>
    * otherise.  Returns <code>null</code> if the accelerometer could not be read.
    * @return True if the accelerometer was shaken, false otherwise, null if read was unsuccessful
    */
   Boolean wasShaken();

   /**
    * Returns <code>true</code> if the BrainLink has been tapped since the last accelerometer read, <code>false</code>
    * otherise.  Returns <code>null</code> if the accelerometer could not be read.
    * @return True if the accelerometer was tapped, false otherwise, null if read was unsuccessful
    */
   Boolean wasTapped();

   /**
    * Returns <code>true</code> if the BrainLink has been shaken or tapped since the last accelerometer read,
    * <code>false</code> otherise.  Returns <code>null</code> if the accelerometer could not be read.
    *
    * @return True if the accelerometer was shaken or tapped, false otherwise, null if read was unsuccessful
    */
   Boolean wasShakenOTapped();

   /** Returns the analog input values; returns <code>null</code> if the inputs could not be read.
    * @return A four element array containing the raw sensor values of the four external analog inputs */

   int[] getAnalogInputs();

   /**
    * Returns the value of the given analog input; returns <code>null</code> if the specified port could not be read or
    * is invalid.
    * @return The raw analog reading (0-255) of one of the four external analog ports, null if the port couldn't be read
    */
   Integer getAnalogInput(int port);

   /** Returns the value of the left light sensor; returns <code>null</code> if the light sensor could not be read.
    * @return The analog value of the left light sensor */
   Integer getLeftLightSensor();

   /** Returns the value of the right light sensor; returns <code>null</code> if the light sensor could not be read.
    * @return The analog value of the right light sensor */
   Integer getRightLightSensor();

   /** Returns the thermistor value; returns <code>null</code> if the thermistor could not be read.
    * @return The raw temperature value of the internal thermometer */
   Integer getThermistor();

   /** Plays a tone specified at the frequency in hertz specified by frequency.  Tone will not stop until turnOffSpeaker is called.
    *
    * @param frequency frequency in Hz of the tone to play
    * @return true if the call was made successfully, false otherwise
    */
   boolean playTone(final int frequency);

   /** Turns off the speaker
    *
    * @return true if the call was made successfully, false otherwise
    */
   boolean turnOffSpeaker();

   /** Initializes the Infrared signal to mimic a given robot's communication protocol specified by initializationBytes.  Currently used by specific robot classes.
    *
    * @param initializationBytes The bytes to send the Brainlink to configure it with a specific IR communication protocol.
    * @return true if the call was made successfully, false otherwise
    */
   boolean initializeIR(final byte[] initializationBytes);

   boolean sendSimpleIRCommand(final SimpleIRCommandStrategy commandStrategy);

   boolean sendSimpleIRCommand(final byte command);

   /** Turns off the IR signal, used in Stop methods in certain robot classes
    *
    * @return true if the call was made successfully, false otherwise
    */
   boolean turnOffIR();

   /** Sends a specific IR command.  Currently used by the specific robot classes.
    *
    * @param commandStrategy An array of bytes encoding the command to send
    * @return true if the call was made successfully, false otherwise
    */
   boolean sendIRCommand(final IRCommandStrategy commandStrategy);
   }