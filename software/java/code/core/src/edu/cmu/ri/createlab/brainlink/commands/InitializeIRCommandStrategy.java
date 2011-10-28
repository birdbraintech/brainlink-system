package edu.cmu.ri.createlab.brainlink.commands;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
// We don't extend CreateLabSerialDeviceNoReturnValueCommandStrategy since we need to define our own execute and write
// methods in order to account for a bug in the firmware which causes the Length of Bit Sequence byte to get sent twice.
public class InitializeIRCommandStrategy extends CustomCommandEchoNoReturnValueCommandStrategy
   {
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
   protected byte[] getCommand()
      {
      return command.clone();
      }

   @Override
   protected byte[] getExpectedCommandEcho()
      {
      return expectedCommandEcho.clone();
      }
   }