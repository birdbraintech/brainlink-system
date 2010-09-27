package edu.cmu.ri.createlab.brainlink;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class BrainLinkConstants
   {
   /** The minimum supported full-color LED intensity */
   public static final int FULL_COLOR_LED_DEVICE_MIN_INTENSITY = 0;

   /** The maximum supported full-color LED intensity */
   public static final int FULL_COLOR_LED_DEVICE_MAX_INTENSITY = 255;

   /** The minimum supported tone frequency */
   public static final int TONE_MIN_FREQUENCY = 0;

   /** The maximum supported tone frequency */
   public static final int TONE_MAX_FREQUENCY = 65535;

   private BrainLinkConstants()
      {
      // private to prevent instantiation
      }
   }
