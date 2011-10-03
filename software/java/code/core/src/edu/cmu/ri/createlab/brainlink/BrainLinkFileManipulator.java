package edu.cmu.ri.createlab.brainlink;

import edu.cmu.ri.createlab.util.ByteUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: tlauwers
 * Date: Sep 30, 2011
 * Time: 10:27:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class BrainLinkFileManipulator {

    private boolean updateData = false;
    private boolean isEncoded;
    private String fileLocation;
    private String fileDirectory = "../devices/";

    HashMap<String, int[]> fileContents = new HashMap<String, int[]>();;

    public BrainLinkFileManipulator(String fileName, boolean encoded)
    {
        fileLocation = fileDirectory + fileName + (encoded?".encsig":".rawsig");
        File storefile = new File(fileLocation);
        isEncoded = encoded;
        if(storefile.exists()) {
            readFileIntoMap();
        }
    }

    public boolean isEmpty() {
        return fileContents.isEmpty();
    }

    public boolean isEncoded() {
        return isEncoded;
    }

    public boolean containsSignal(String signalName) {
        return fileContents.containsKey(signalName);
    }

    public boolean storeEncodedSignal(String signalName, int[] values, int repeatTime)
    {
        try {
            File storefile = new File(fileLocation);
            if(!storefile.exists()) {
                System.out.println("File doesn't exist, provide initialization data first.");
                return false;
            }
            if(repeatTime < 0 || repeatTime > 132) {
                System.out.println("Repeat time is not in valid range of 0 to 132.");
                return false;
            }

            removeLineFromFile(fileLocation, signalName);
            FileOutputStream fileout = new FileOutputStream(fileLocation, true);
            Writer out = new OutputStreamWriter(fileout, "UTF-8");
            String signalCommands = signalName+";";
            for(int i = 0; i < values.length; i++) {
                signalCommands +=  ByteUtils.intToUnsignedByte(values[i]) + ";";
            }
            signalCommands += getHighByteFromInt(repeatTime*500) + ";" + getLowByteFromInt(repeatTime*500);

            out.append(signalCommands+"\n");
            out.close();
            fileout.close();
            updateData = true;
            return true;
        }
        catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean storeRawSignal(String signalName, int[] values, int repeatTime)
    {
        try {
            if(repeatTime > 132 || repeatTime < 0) {
                System.out.println("Repeat time is not in valid range of 0 to 132.");
                return false;
            }
            if(fileContents.containsKey(signalName)) {
                removeLineFromFile(fileLocation, signalName);
            }
            FileOutputStream fileout = new FileOutputStream(fileLocation, true);
            Writer out = new OutputStreamWriter(fileout, "UTF-8");
            out.append(signalName);
            out.append(";"+values.length);
            for(int i = 0; i < values.length; i++) {
                out.append(";"+values[i]);
            }
            out.append(";" + repeatTime);
            out.append("\n");
            out.close();
            fileout.close();
            updateData = true;
            return true;
        }
        catch(IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean storeInitialization(int carrierFreq, int[] startUpPulses, int numBits, int logicalOneTime, int logicalZeroTime, int bitSpacing, String encoding)
    {
        try {
            if(startUpPulses.length%2 == 0) {
                System.out.println("Should have an odd number of startup pulses");
                return false;
            }

            File storefile = new File(fileLocation);
            if(storefile.exists()) {
                removeLineFromFile(fileLocation, "Initialization;");
            }
            FileOutputStream fileout = new FileOutputStream(fileLocation, true);
            Writer out = new OutputStreamWriter(fileout, "UTF-8");
            String initCommands = "Initialization;";
            // Start by giving it the values for the carrier
            int tempCarrier = 32000000/carrierFreq;
            initCommands += getHighByteFromInt(tempCarrier) + ";" + getLowByteFromInt(tempCarrier) + ";";
            initCommands += startUpPulses.length + ";";
            for(int i = 0; i < startUpPulses.length; i++) {
                initCommands +=  getHighByteFromInt(startUpPulses[i]/2) + ";" + getLowByteFromInt(startUpPulses[i]/2) + ";";
            }
            if(encoding.equals("alternating")) {
                initCommands += "0;";
            }
            else if(encoding.equals("uptime")) {
                initCommands += "1;";
            }
            else if(encoding.equals("downtime")) {
                initCommands += "2;";
            }
            else if(encoding.equals("irobot")) {
                initCommands += "3;";
            }
            else {
                System.out.println("Invalid encoding style, initialization not written to file");
                return false;
            }
            initCommands += numBits + ";";
            initCommands += getHighByteFromInt(logicalOneTime/2) + ";" + getLowByteFromInt(logicalOneTime/2) + ";";
            initCommands += getHighByteFromInt(logicalZeroTime/2) + ";" + getLowByteFromInt(logicalZeroTime/2) + ";";
            initCommands += getHighByteFromInt(bitSpacing/2) + ";" + getLowByteFromInt(bitSpacing/2);

            out.append(initCommands+"\n");
            out.close();
            fileout.close();
            updateData = true;
            return true;
         }
        catch (FileNotFoundException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setFileDirectory(String location)
    {
        fileDirectory = location;
    }

    public String getFileLocation()
    {
        return fileLocation;
    }

    public String[] getSignalNames()
    {
         if(updateData) {
             readFileIntoMap();
             updateData = false;
         }
         if(fileContents.isEmpty()) {
             System.out.println("Warning, file is empty");
             return null;
         }
         String [] ret = new String[fileContents.size()];
         fileContents.keySet().toArray(ret);
         
         return ret;
         
    }

    public byte[] getInitialization()
    {
        if(updateData) {
             readFileIntoMap();
             updateData = false;
        }
        if(fileContents.isEmpty()) {
             System.out.println("Warning, file is empty");
             return null;
        }
        int[] initData = fileContents.get("Initialization");
        byte [] returnData = new byte[initData.length];
        for(int i = 0; i < initData.length; i++) {
            returnData[i] =  (byte)initData[i];
        }
        return returnData;

    }

    public String[] getHumanReadableInitialization()
    {
        if(updateData) {
             readFileIntoMap();
             updateData = false;
        }
        if(fileContents.isEmpty()) {
             System.out.println("Warning, file is empty");
             return null;
        }
        int[] initData = fileContents.get("Initialization");
        if(initData == null)
        {
            System.out.println("Warning, file has no initialization. Maybe it's raw instead of encoded?");
            return null;
        }
        String [] returnData = new String[(initData.length+1)/2];

        returnData[0] = Integer.toString((32000000/(initData[0] * 256 + initData[1])));
        int startUpPulseLength = initData[2]*2;
        switch(initData[3+startUpPulseLength])
        {
             case 0:
                 returnData[1] = "alternating";
                 break;
             case 1:
                 returnData[1] = "uptime";
                 break;
             case 2:
                 returnData[1] = "downtime";
                 break;
             case 3:
                 returnData[1] = "irobot";
                 break;
             default:
                 returnData[1] = "invalid";
                 break;
        }
        returnData[2] = Integer.toString(initData[4+startUpPulseLength]);
        returnData[3] = Integer.toString(initData[5+startUpPulseLength] * 256 + initData[6+startUpPulseLength]);
        returnData[4] = Integer.toString(initData[7+startUpPulseLength] * 256 + initData[8+startUpPulseLength]);
        returnData[5] = Integer.toString(initData[9+startUpPulseLength] * 256 + initData[10+startUpPulseLength]);
        for(int i = 0; i < initData[2]; i++) {
            returnData[i+6] = Integer.toString(initData[3+i*2] * 256 + initData[4+i*2]);
        }
        return returnData;
    }

    public int[] getSignalValues(String signalName)
    {
        if(updateData) {
            readFileIntoMap();
            updateData = false;
        }
        if(fileContents.isEmpty()) {
            System.out.println("Warning, file is empty");
            return null;
        }
        if(fileContents.containsKey(signalName)) {
            int[] signalValuesWithRepeat = fileContents.get(signalName);
            if(isEncoded) {
                int[] signalValues = new int[signalValuesWithRepeat.length-2];
                System.arraycopy(signalValuesWithRepeat, 0, signalValues, 0, signalValuesWithRepeat.length-2);
                return signalValues;
            }
            else {
                int[] signalValues = new int[signalValuesWithRepeat.length-1];
                System.arraycopy(signalValuesWithRepeat, 0, signalValues, 0, signalValuesWithRepeat.length-1);
                return signalValues;
            }
        }
        else {
             System.out.println("Warning, signal name doesn't exist in file");
             return null;
         }

    }

    public Integer getSignalRepeatTime(String signalName)
    {
         if(updateData) {
             readFileIntoMap();
             updateData = false;
         }
          if(fileContents.isEmpty()) {
             System.out.println("Warning, file is empty");
             return null;
         }
        if(isEncoded) {
            int[] signalValues = fileContents.get(signalName);
            return ((signalValues[signalValues.length-2]*256 + signalValues[signalValues.length-1])/500);
        }
        else {
            int[] signalValues = fileContents.get(signalName);
            return signalValues[signalValues.length-1];

        }
    }

    public String getFileDirectory()
    {
        return fileDirectory;
    }

    private boolean readFileIntoMap()
    {
        fileContents.clear();

        try {
           // Open the file for reading
           Scanner scanner = new Scanner(new FileInputStream(fileLocation), "UTF-8");
           scanner.useDelimiter(";");

           String temp;   // Temporarily holds a line of the file for parsing
           String signalName;  // Holds the name of the signal
           int semiColonIndex;  // Signal names are always between the new line and first semi-colon
           while(scanner.hasNext()) {
              temp = scanner.nextLine();
              semiColonIndex = temp.indexOf(";");
              if(semiColonIndex > 0) {
                  signalName = temp.substring(0, semiColonIndex);
                   // Get just the signals
                  temp = temp.substring(semiColonIndex+1);
                   // Split into a whole bunch of strings, each representing a signal
                  String[] signalValues = temp.split(";");
                  int[] signalValuesAsInts = new int[signalValues.length];
                   for(int i = 0; i < signalValues.length; i++) {
                       signalValuesAsInts[i] = Integer.valueOf(signalValues[i]);
                       if(signalValuesAsInts[i] < 0) {
                            signalValuesAsInts[i] += 256;
                       }
                   }
                   fileContents.put(signalName, signalValuesAsInts);
              }
           }
           return true;
           
        }
        catch (FileNotFoundException e) {
            System.out.println("readFile Warning: No file by that name");
            return false;
        }
        catch(IOException e) {
            System.out.println("readFile Warning: Failed to read, generic IO Exception");
            return false;
        }
    }

    public void removeLineFromFile(String file, String lineToRemove) {

       try {

         File inFile = new File(file);

         if (!inFile.isFile()) {
           System.out.println("Parameter is not an existing file");
           return;
         }

         //Construct the new file that will later be renamed to the original filename.
         File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

         BufferedReader br = new BufferedReader(new FileReader(file));
         PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

         String line = null;

         //Read from the original file and write to the new
         //unless content matches data to be removed.
         while ((line = br.readLine()) != null) {

           if (!line.startsWith(lineToRemove)) {

             pw.println(line);
             pw.flush();
           }
         }
         pw.close();
         br.close();

         //Delete the original file
         if (!inFile.delete()) {
           System.out.println("Could not delete file");
           return;
         }

         //Rename the new file to the filename the original file had.
         if (!tempFile.renameTo(inFile))
           System.out.println("Could not rename file");

       }
       catch (FileNotFoundException ex) {
         ex.printStackTrace();
       }
       catch (IOException ex) {
         ex.printStackTrace();
       }
     }

       private byte getHighByteFromInt(final int val)
         {
         return (byte)((val << 16) >> 24);
         }

      private byte getLowByteFromInt(final int val)
         {
         return (byte)((val << 24) >> 24);
         }


}
