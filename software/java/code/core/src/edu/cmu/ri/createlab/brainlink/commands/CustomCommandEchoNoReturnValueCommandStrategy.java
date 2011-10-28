package edu.cmu.ri.createlab.brainlink.commands;

import java.io.IOException;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceCommandStrategy;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandResponse;
import edu.cmu.ri.createlab.serial.SerialDeviceIOHelper;
import edu.cmu.ri.createlab.util.ByteUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>CustomCommandEchoNoReturnValueCommandStrategy</code> is similar to {@link CreateLabSerialDeviceNoReturnValueCommandStrategy}
 * but allows subclasses to define a custom command echo, for cases where the echo is not simply identical to the command.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class CustomCommandEchoNoReturnValueCommandStrategy extends CreateLabSerialDeviceCommandStrategy
   {
   private static final Logger LOG = Logger.getLogger(CustomCommandEchoNoReturnValueCommandStrategy.class);

   protected abstract byte[] getCommand();

   protected abstract byte[] getExpectedCommandEcho();

   @Override
   public final SerialDeviceCommandResponse execute(final SerialDeviceIOHelper ioHelper) throws Exception
      {
      // write the command and check for the command echo
      final boolean wasSuccessful = writeCommand(ioHelper, getCommand(), getExpectedCommandEcho());

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
               LOG.warn("CustomCommandEchoNoReturnValueCommandStrategy.writeCommand(): failed to write command, will" + (numWrites < getMaxNumberOfRetries() ? " " : " not ") + "retry");
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
            LOG.trace("CustomCommandEchoNoReturnValueCommandStrategy.writeCommandWorkhorse(): Writing the command [" + s + "]...");
            }

         ioHelper.write(command);

         LOG.trace("CustomCommandEchoNoReturnValueCommandStrategy.writeCommandWorkhorse(): Listening for command echo...");

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
                  LOG.trace("CustomCommandEchoNoReturnValueCommandStrategy.writeCommandWorkhorse():    read [" + (char)actual + "|" + actual + "]");
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
                        LOG.warn("CustomCommandEchoNoReturnValueCommandStrategy.writeCommandWorkhorse(): Mismatch detected: expected [" + ByteUtils.unsignedByteToInt(expected) + "], but read [" + ByteUtils.unsignedByteToInt(actualAsByte) + "]");
                        }
                     isMatch = false;
                     break;
                     }
                  }
               else
                  {
                  LOG.error("CustomCommandEchoNoReturnValueCommandStrategy.writeCommandWorkhorse(): End of stream reached while trying to read the command");
                  break;
                  }
               }
            }

         final boolean echoDetected = (pos == expectedCommandEcho.length) && isMatch;
         if (LOG.isTraceEnabled())
            {
            LOG.trace("CustomCommandEchoNoReturnValueCommandStrategy.writeCommandWorkhorse(): Command echo detected = " + echoDetected + " (isMatch=[" + isMatch + "], expected length=[" + expectedCommandEcho.length + "], actual length=[" + pos + "])");
            }

         return echoDetected;
         }
      catch (IOException e)
         {
         LOG.error("CustomCommandEchoNoReturnValueCommandStrategy.writeCommandWorkhorse(): IOException while trying to read the command", e);
         }

      return false;
      }
   }
