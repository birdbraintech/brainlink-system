package edu.cmu.ri.createlab.brainlink.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandResponse;
import edu.cmu.ri.createlab.serial.SerialDeviceIOHelper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>ThrottledNoReturnValueCommandStrategy</code> is a {@link CreateLabSerialDeviceCommandStrategy} (with no return
 * value) which can be configured to pause for a given number of milliseconds after every N bytes written.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class ThrottledNoReturnValueCommandStrategy extends CreateLabSerialDeviceCommandStrategy
   {
   private static final Logger LOG = Logger.getLogger(ThrottledNoReturnValueCommandStrategy.class);

   private final int pauseInMillisBetweenChunksWritten;
   private final int chunkSizeInBytes;

   protected ThrottledNoReturnValueCommandStrategy(final int pauseInMillisBetweenChunksWritten, final int chunkSizeInBytes)
      {
      this.pauseInMillisBetweenChunksWritten = Math.max(pauseInMillisBetweenChunksWritten, 0);
      this.chunkSizeInBytes = Math.max(chunkSizeInBytes, 0);
      }

   @Override
   public final SerialDeviceCommandResponse execute(final SerialDeviceIOHelper serialDeviceIOHelper)
      {
      // get the command to be written
      final byte[] command = getCommand();

      // only use a ThrottledSerialDeviceIOHelper if both the pause and num bytes between chunks are non-zero
      final SerialDeviceIOHelper ioHelper = (pauseInMillisBetweenChunksWritten == 0 || chunkSizeInBytes == 0) ? serialDeviceIOHelper : new ThrottledSerialDeviceIOHelper(serialDeviceIOHelper);

      // write the command and check for the command echo
      final boolean wasSuccessful = writeCommand(ioHelper, command);

      // return the response
      return new SerialDeviceCommandResponse(wasSuccessful);
      }

   protected abstract byte[] getCommand();

   private class ThrottledSerialDeviceIOHelper implements SerialDeviceIOHelper
      {
      private final SerialDeviceIOHelper ioHelper;

      private ThrottledSerialDeviceIOHelper(final SerialDeviceIOHelper ioHelper)
         {
         this.ioHelper = ioHelper;
         }

      @Override
      @SuppressWarnings({"BusyWait"})
      public void write(final byte[] data) throws IOException
         {
         if (data != null && data.length > 0)
            {
            if (pauseInMillisBetweenChunksWritten == 0 || chunkSizeInBytes == 0)
               {
               ioHelper.write(data);
               }
            else
               {
               final OutputStream out = getOutputStream();

               int numBytesWritten = 0;
               while (numBytesWritten < data.length)
                  {
                  final int numBytesToWriteInThisChunk = Math.min(chunkSizeInBytes, data.length - numBytesWritten);

                  try
                     {
                     // write this chunk
                     out.write(data, numBytesWritten, numBytesToWriteInThisChunk);
                     out.flush();
                     numBytesWritten += numBytesToWriteInThisChunk;

                     // pause
                     try
                        {
                        Thread.sleep(pauseInMillisBetweenChunksWritten);
                        }
                     catch (InterruptedException e)
                        {
                        LOG.error("InterruptedException while sleeping", e);
                        }
                     }
                  catch (IOException e)
                     {
                     LOG.error("Caught IOException while trying to write the data.  Rethrowing...", e);
                     throw e;
                     }
                  catch (Exception e)
                     {
                     if (LOG.isEnabledFor(Level.ERROR))
                        {
                        LOG.error("Caught " + e.getClass() + " while trying to write the data.  Rethrowing as an IOException.", e);
                        }
                     throw new IOException(e.getMessage());
                     }
                  }
               }
            }
         }

      @Override
      public int available() throws IOException
         {
         return ioHelper.available();
         }

      @Override
      public boolean isDataAvailable() throws IOException
         {
         return ioHelper.isDataAvailable();
         }

      @Override
      public InputStream getInputStream()
         {
         return ioHelper.getInputStream();
         }

      @Override
      public OutputStream getOutputStream()
         {
         return ioHelper.getOutputStream();
         }

      @Override
      public int read() throws IOException
         {
         return ioHelper.read();
         }

      @Override
      public int read(final byte[] buffer) throws IOException
         {
         return ioHelper.read(buffer);
         }
      }
   }
