package edu.cmu.ri.createlab.brainlink.commands;

import java.io.IOException;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandResponse;
import edu.cmu.ri.createlab.serial.SerialDeviceIOHelper;
import edu.cmu.ri.createlab.util.ByteUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
// We don't extend CreateLabSerialDeviceNoReturnValueCommandStrategy since we need to define our own execute and write
// methods in order to account for a bug in the firmware which causes the Length of Bit Sequence byte to get sent twice.
public class InitializeIRCommandStrategy extends CreateLabSerialDeviceCommandStrategy
   {
   private static final Logger LOG = Logger.getLogger(InitializeIRCommandStrategy.class);

   /** The command character used to initialize the IR. */
   private static final byte COMMAND_PREFIX = 'I';

   private final byte[] command;
   private final byte[] expectedCommandEcho;

   public InitializeIRCommandStrategy(final byte[] initializationBytes)
      {
      this.command = new byte[initializationBytes.length + 1];
      this.command[0] = COMMAND_PREFIX;
      System.arraycopy(initializationBytes, 0, this.command, 1, initializationBytes.length);

      // Build the expected response.  Normally, this would be identical to the command since the CREATE Lab protocol
      // defines the response to simply be an echo.  However, there's a bug in the current firmware which causes the
      // byte specifying the Length of Bit Sequence to get sent twice.  So, the expected echo will be one byte longer
      // than the command.  The byte containing the Length of Bit Sequence is the 7th byte from the end.
      this.expectedCommandEcho = new byte[command.length + 1];
      System.arraycopy(command, 0, expectedCommandEcho, 0, command.length);   // make an exact copy

      // copy the final 7 bytes again, but shifted 1 towards the end, giving us the duplicated byte
      final int byteNumberToStartCopy = command.length - 7;
      System.arraycopy(command, byteNumberToStartCopy, expectedCommandEcho, byteNumberToStartCopy + 1, 7);
      }

   @Override
   public SerialDeviceCommandResponse execute(final SerialDeviceIOHelper ioHelper) throws Exception
      {
      // write the command and check for the command echo
      final boolean wasSuccessful = writeCommand(ioHelper, command.clone(), expectedCommandEcho.clone());

      // return the response
      return new SerialDeviceCommandResponse(wasSuccessful);
      }

   private boolean writeCommand(final SerialDeviceIOHelper ioHelper, final byte[] command, final byte[] expectedCommandEcho)
      {
      // initialize the retry count
      int numWrites = 0;

      boolean echoDetected;
      do
         {
         echoDetected = writeCommandWorkhorse(ioHelper, command, expectedCommandEcho);
         numWrites++;
         if (!echoDetected)
            {
            if (LOG.isEnabledFor(Level.WARN))
               {
               LOG.warn("InitializeIRCommandStrategy.writeCommand(): failed to write command, will" + (numWrites < getMaxNumberOfRetries() ? " " : " not ") + "retry");
               }
            slurp(ioHelper);
            }
         }
      while (!echoDetected && numWrites < getMaxNumberOfRetries());

      return echoDetected;
      }

   private boolean writeCommandWorkhorse(final SerialDeviceIOHelper ioHelper, final byte[] command, final byte[] expectedCommandEcho)
      {
      try
         {
         if (LOG.isTraceEnabled())
            {
            final StringBuffer s = new StringBuffer("[");
            for (final byte b : command)
               {
               s.append("(").append((char)b).append("|").append(ByteUtils.unsignedByteToInt(b)).append(")");
               }
            s.append("]");
            LOG.trace("InitializeIRCommandStrategy.writeCommandWorkhorse(): Writing the command [" + s + "]...");
            }

         ioHelper.write(command);

         LOG.trace("InitializeIRCommandStrategy.writeCommandWorkhorse(): Listening for command echo...");

         // initialize the counter for reading from the command
         int pos = 0;

         // initialize the flag which tracks whether the command was correctly echoed
         boolean isMatch = true;

         // define the ending time
         final long endTime = getReadTimeoutMillis() + System.currentTimeMillis();
         while ((pos < expectedCommandEcho.length) && (System.currentTimeMillis() <= endTime))
            {
            if (ioHelper.isDataAvailable())
               {
               final byte expected = expectedCommandEcho[pos];
               final int actual = ioHelper.read();
               pos++;                                 // increment the read counter

               if (LOG.isTraceEnabled())
                  {
                  LOG.trace("InitializeIRCommandStrategy.writeCommandWorkhorse():    read [" + (char)actual + "|" + actual + "]");
                  }

               // see if we reached the end of the stream
               if (actual >= 0)
                  {
                  final byte actualAsByte = (byte)actual;
                  // make sure this character in the command matches; break if not
                  if (expected != actualAsByte)
                     {
                     if (LOG.isEnabledFor(Level.WARN))
                        {
                        LOG.warn("InitializeIRCommandStrategy.writeCommandWorkhorse(): Mismatch detected: expected [" + ByteUtils.unsignedByteToInt(expected) + "], but read [" + ByteUtils.unsignedByteToInt(actualAsByte) + "]");
                        }
                     isMatch = false;
                     break;
                     }
                  }
               else
                  {
                  LOG.error("InitializeIRCommandStrategy.writeCommandWorkhorse(): End of stream reached while trying to read the command");
                  break;
                  }
               }
            }

         final boolean echoDetected = (pos == expectedCommandEcho.length) && isMatch;
         if (LOG.isTraceEnabled())
            {
            LOG.trace("InitializeIRCommandStrategy.writeCommandWorkhorse(): Command echo detected = " + echoDetected + " (isMatch=[" + isMatch + "], expected length=[" + expectedCommandEcho.length + "], actual length=[" + pos + "])");
            }

         return echoDetected;
         }
      catch (IOException e)
         {
         LOG.error("InitializeIRCommandStrategy.writeCommandWorkhorse(): IOException while trying to read the command", e);
         }

      return false;
      }
   }