package edu.cmu.ri.createlab.brainlink.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceHandshakeCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HandshakeCommandStrategy extends CreateLabSerialDeviceHandshakeCommandStrategy
   {
   /** The pattern of characters to look for in the BrainLink's startup mode "song" */
   private static final byte[] STARTUP_MODE_SONG_CHARACTERS = {'B', 'L'};

   /** The pattern of characters to send to put the BrainLink into receive mode. */
   private static final byte[] RECEIVE_MODE_CHARACTERS = {'*'};

   public HandshakeCommandStrategy()
      {
      super(5000, DEFAULT_SLURP_TIMEOUT_MILLIS, DEFAULT_MAX_NUMBER_OF_RETRIES);
      }

   protected byte[] getReceiveModeCharacters()
      {
      return RECEIVE_MODE_CHARACTERS.clone();
      }

   protected byte[] getStartupModeCharacters()
      {
      return STARTUP_MODE_SONG_CHARACTERS.clone();
      }
   }
