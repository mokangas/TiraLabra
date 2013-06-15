package compression;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * This is for reading and writing the compressed pictures, a.k.a. wtf-files.
 *
 * The file header has the following information 
 * ------------------------- 
 * byte Type of the file, tells how the rest of the information is coded 
 * short No of colors = 3 // for possible alterations 
 * int Width of the original picture 
 * int height of the original picture 
 * short level of loss 
 * int length of one line in the compressed form. 
 * ====================== 
 * The Integer form has all the data
 * written as integers: t[0][0][0], t[0][0][1], t[0][0][2], t[0][0][3],... It's
 * file type (the first byte of the file) is 0.
 *
 * In the mixed form every line has first two integers, shortOffset tells how
 * manyth is the first coefficient that is of the type short. intOffset tells
 * how manyth is the first coefficient that is of the type int. The data will be
 * bytes until the index shortOffset is reached, then shorts until intOffset is
 * reached, and all the rest are integers. Mixed form's file type is 1.
 */
public class WTFIO {

    private DataInputStream reader;
    private byte typeOfFile;
    private short noOfColors;
    private int originalWidth;
    private int originalHeight;
    private short levelOfLoss;
    private int compressedHeight;

    /**
     * This is for creating a new WTFIO-object, which has to be done only when
     * reading data. To write down data you can use the static methods.
     *
     * @param file The file from which the data will be read.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public WTFIO(File file) throws FileNotFoundException, IOException {
        reader = new DataInputStream(new FileInputStream(file));
        typeOfFile = reader.readByte();
        noOfColors = reader.readShort();
        originalWidth = reader.readInt();
        originalHeight = reader.readInt();
        levelOfLoss = reader.readShort();
        compressedHeight = reader.readInt();
    }

    /**
     * This writes a wtf-file on the hard disk as "integer data", which is
     * specified in the description of this class. It corresponds to file type 0.
     * Every coefficient of the transform will be written down as an integer, so
     * this is a particularly wasteful way to do it.
     *
     * @param transform The transform to be written down.
     * @param originalHeight The height of the original picture before the
     * transform. In more general use, this is the length of the compressed data
     * array.
     * @param levelOfLoss The loss coefficient: how many levels of wavelets is
     * omitted in the transform.
     * @param outputFile The file to which the data will be written.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void writeIntegerData(int[][][] transform, int originalHeight,
            int levelOfLoss, File outputFile) throws FileNotFoundException, IOException {

        DataOutputStream writer = new DataOutputStream(new FileOutputStream(outputFile));
        createHeader(transform, originalHeight, levelOfLoss, writer, (byte) 0);

        for (int i = 0; i < transform.length; i++) {
            for (int j = 0; j < transform[0].length; j++) {
                for (int k = 0; k < transform[0][0].length; k++) {
                    writer.writeInt(transform[i][j][k]);
                }
            }
        }

        writer.flush();
        writer.close();

    }

    /**
     * This writes a wtf-file on the hard disk as "mixed data", which is
     * specified in the description of this class. Each line of the transform
     * will be written down as bytes, shorts and ints in that order, so that
     * after the first short only shorts and ints occur, and after the first int
     * only ints.
     *
     * @param transform The transform to be written down.
     * @param originalHeight The height of the original picture before the
     * transform. In more general use, this is the length of the compressed data
     * array.
     * @param levelOfLoss The loss coefficient: how many levels of wavelets is
     * omitted in the transform.
     * @param outputFile The file to which the data will be written.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void writeMixedData(int[][][] transform, int originalHeight,
            int levelOfLoss, File file) throws FileNotFoundException, IOException {

        DataOutputStream writer = new DataOutputStream(new FileOutputStream(file));
        createHeader(transform, originalHeight, levelOfLoss, writer, (byte) 1);

        for (int i = 0; i < transform.length; i++) {
            for (int j = 0; j < transform[0].length; j++) {

                byte[] line = createLineOfMixedData(transform[i][j]);
                writer.write(line);

            }
        }

    }

    /**
     * This is an auxiliary method for writing the header of the wtf-file.
     *
     * @param transform The content of the transform
     * @param originalHeight The original height of the picture, or more
     * generally, the length of the line that was compressed.
     * @param levelOfLoss The level of loss in the compression.
     * @param writer The output stream into which the header will be written.
     * @param type The type of the wtf-file.
     * @throws IOException
     */
    private static void createHeader(int[][][] transform,
            int originalHeight, int levelOfLoss, DataOutputStream writer, byte type) throws IOException {

        int noOfColors = transform.length;
        int width = transform[0].length;
        int compressedLength = transform[0][0].length;

        writer.writeByte(type);
        writer.writeShort((short) transform.length);
        writer.writeInt(width);
        writer.writeInt(originalHeight);
        writer.writeShort((short) levelOfLoss);
        writer.writeInt(compressedLength);
    }

    /**
     * Tells the type of the wtf-file. Different types are described in the
     * description of this class.
     *
     * @return The type of the wtf-file
     */
    public byte getTypeOfFile() {
        return typeOfFile;
    }

    /**
     *
     * @return number of colors in the transformed picture.
     */
    public short getNoOfColors() {
        return noOfColors;
    }

    /**
     *
     * @return The width of the transformed picture.
     */
    public int getOriginalWidth() {
        return originalWidth;
    }

    /**
     *
     * @return The height of the transformed picture.
     */
    public int getOriginalHeight() {
        return originalHeight;
    }

    /**
     *
     * @return The level of loss of the transform.
     */
    public short getLevelOfLoss() {
        return levelOfLoss;
    }

    /**
     *
     * @return The length of the compressed line.
     */
    public int getCompressedHeight() {
        return compressedHeight;
    }

    /**
     * Reads a wtf-file that is written down as "integer data", i.e. that has
     * type 0, as explained in the description of this class.
     *
     * @return the transform that is the content of the file.
     * @throws IOException
     */
    public int[][][] readIntegerData() throws IOException {
        int[][][] transformData = new int[noOfColors][originalWidth][compressedHeight];
        for (int i = 0; i < noOfColors; i++) {
            for (int j = 0; j < originalWidth; j++) {
                for (int k = 0; k < compressedHeight; k++) {
                    transformData[i][j][k] = reader.readInt();
                }
            }
        }

        reader.close();
        return transformData;
    }

    /**
     * Reads a wtf-file that is written down as "mixed data", i.e. that has
     * type 1 as explained in the description of this class.
     *
     * @return the transform that is the content of the file.
     * @throws IOException
     */

    public int[][][] readMixedData() throws IOException {

        int[][][] transformData = new int[noOfColors][originalWidth][compressedHeight];

        for (int i = 0; i < noOfColors; i++) {
            for (int j = 0; j < originalWidth; j++) {

                int[] offSets = readOffsets();
                ByteBuffer buffer = readLine(offSets);
                decodeRawMixedDataLine(buffer, transformData[i][j], offSets);

            }
        }

        reader.close();
        return transformData;

    }
    
    /**
     * An auxiliary method to read offsets from the start of a line in mixed data.
     * @return The offsets as an array {shortOffset, intOffset}.
     * @throws IOException 
     */
    public int[] readOffsets() throws IOException{
        int[] offSets = new int[2];
                ByteBuffer buffer = ByteBuffer.allocate(8);
                for (int k = 0; k < 8; k++) {
                    buffer.put(reader.readByte());
                }
                
                for (int k = 0; k < 2; k++) {
                    offSets[k] = buffer.getInt(4*k);
                }
                
                return offSets;
    }

    /**
     * Reads a line of mixed data.
     * @param offSets The short and int offsets of the line.
     * @return The line of compressed data in a ByteBuffer.
     * @throws IOException 
     */
    public ByteBuffer readLine(int[] offSets) throws IOException {

        int length = offSets[0] + 2 * (offSets[1] - offSets[0]) + 4 * (compressedHeight - offSets[1]);

        byte[] readLine = new byte[length];
        reader.read(readLine);
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(readLine);
        
        return buffer;
    }
   
    /**
     * Decodesa line of raw mixed data to the approprite data types.
     * @param buffer The ByteBuffer where the raw data is stored.
     * @param transformDataLine The array to which the decoded information is stored.
     * @param offSets The offsets of short and integer data types. Until offSets[0]
     * numbers are stored as bytes, after it as shorts, and after offSets[1] as ints.
     */
    public void decodeRawMixedDataLine(ByteBuffer buffer, int[] transformDataLine, int[] offSets){
        
                buffer.position(0);
                for (int k = 0; k < offSets[0]; k++) {
                    transformDataLine[k] = (int) buffer.get();
                }
                for (int k = offSets[0]; k < offSets[1]; k++) {
                    transformDataLine[k] = (int) buffer.getShort();
                }
                for (int k = offSets[1]; k < compressedHeight; k++) {
                    transformDataLine[k] = (int) buffer.getInt();
                }
    }
    
    /**
     * Reads the data in the file. This method figures out by itself how the data is written. 
     * @return The transform data contained in the wtf file.
     * @throws IOException 
     */
    public int[][][] readData() throws IOException {
        switch (typeOfFile) {
            case 0:
                return readIntegerData();
            case 1:
                return readMixedData();
        }

        return null;
    }

    /**
     * An auxiliary method for the writeMixedData-method. Calculates when on the
     * line the occurs the first coefficients that are too big for byte and
     * short data types. If no coefficient requires one of these data types, it's
     * offsets will be the length of the data array.
     *
     * @param data A line of the compressed data.
     * @return The offsets of shorts and integers respectively.
     */
    public static int[] calculateOffsets(int[] data) {

        int[] offsets = new int[2];
        offsets[0] = data.length;
        offsets[1] = data.length;
        boolean shortFound = false;
        boolean intFound = false;
        int minByte = (int) Byte.MIN_VALUE;
        int maxByte = (int) Byte.MAX_VALUE;
        int minShort = (int) Short.MIN_VALUE;
        int maxShort = (int) Short.MAX_VALUE;

        for (int i = 0; i < data.length; i++) {
            int read = data[i];
            if (!shortFound && (read < minByte || read > maxByte)) {
                offsets[0] = i;
                shortFound = true;
            }
            if (!intFound && (read < minShort || read > maxShort)) {
                offsets[1] = i;
                if (!shortFound) {
                    offsets[0] = i;
                }
                break;
            }
        }

        return offsets;
    }
    
    /**
     * Creates a line of mixed data complete with the offsets.
     * @param data The data to be written to the file.
     * @return The data as it will written in the file.
     */
    
    public static byte[] createLineOfMixedData(int[] data){
        
        
        int[] offSets = calculateOffsets(data);

        // Calculate the length of the result array:
        int noOfBytes = offSets[0];
        int noOfShorts = offSets[1] - offSets[0];
        int noOfInts = data.length - offSets[1];
        int length = 8 + noOfBytes + 2 * noOfShorts + 4 * noOfInts;
        
        ByteBuffer buffer = ByteBuffer.allocate(length);
        
        // Write down the offsets, the bytes, the shorts and the ints:
        for (int i = 0; i < 2; i++) {
            buffer.putInt(offSets[i]);
        }
        
        for (int i = 0; i < offSets[0]; i++) {
            buffer.put( (byte) data[i]);
        }
        
        for (int i = offSets[0]; i < offSets[1]; i++) {
            buffer.putShort((short) data[i]);
        }
        
        for (int i = offSets[1]; i < data.length; i++) {
            buffer.putInt(data[i]);
        }
       
        return buffer.array();
    }

}
