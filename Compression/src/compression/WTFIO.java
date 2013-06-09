package compression;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * This is for reading and writing the compressed pictures, a.k.a. wtf-files.
 *
 * The file header has the following information ------------------------- byte
 * Type of the file, tells how the rest of the information is coded short No of
 * colors = 3 // for possible alterations int Width of the original picture int
 * height of the original picture short level of loss int length of one line in
 * the compressed form. ====================== The Integer form has all the data
 * written as integers: t[0][0][0], t[0][0][1], t[0][0][2], t[0][0][3],... It's
 * type (the first byte of the file) is 0.
 *
 * In mixed form every line has first two integers, short offset tells how
 * manyth is the first coefficient that is of the type short. int offset tells
 * how manyth is the first coefficient that is of the type int. The data will be
 * bytes until the index shortOffset is reached, then shorts until intOffset is
 * reached, and all the rest are integers. Mixed form's type is 1.
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
     * specified in the description of this class. It corresponds to type 0.
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

//                int offSets[] = calculateOffsets(transform[i][j]);
//                writer.writeInt(offSets[0]);
//                writer.writeInt(offSets[1]);
//                
//                for (int k = 0; k < offSets[0]; k++) {
//                    writer.writeByte((byte) transform[i][j][k]);
//                }
//                for (int k = offSets[0]; k < offSets[1]; k++) {
//                    writer.writeShort((short) transform[i][j][k]);
//                }
//                for (int k = offSets[1]; k < transform[0][0].length; k++) {
//                    writer.writeInt(transform[i][j][k]);
//                }
            }
        }

    }

    /**
     * This is an auxiliary method for writing the header of the wtf-file.
     *
     * @param transform The content of the transform
     * @param originalHeight The original height of the picturem or more
     * generally, the length of the compressed line.
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
     * type 0 as explained in the description of this class.
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
     * Reads a wtf-file that is written down as "integer data", i.e. that has
     * type 1 as explained in the description of this class.
     *
     * @return the transform that is the content of the file.
     * @throws IOException
     */
    public int[][][] readMixedDataOriginal() throws IOException {

        int[][][] transformData = new int[noOfColors][originalWidth][compressedHeight];
        int shortOffset = -1;
        int intOffset = -1;
        for (int i = 0; i < noOfColors; i++) {
            for (int j = 0; j < originalWidth; j++) {

                shortOffset = reader.readInt();
                intOffset = reader.readInt();

                for (int k = 0; k < shortOffset; k++) {
                    transformData[i][j][k] = (int) reader.readByte();
                }
                for (int k = shortOffset; k < intOffset; k++) {
                    transformData[i][j][k] = (int) reader.readShort();
                }
                for (int k = intOffset; k < compressedHeight; k++) {
                    transformData[i][j][k] = reader.readInt();
                }
            }
        }

        reader.close();
        return transformData;

    }

    public int[][][] readMixedData() throws IOException {

        int[][][] transformData = new int[noOfColors][originalWidth][compressedHeight];
        
        for (int i = 0; i < noOfColors; i++) {
            for (int j = 0; j < originalWidth; j++) {
                int[] offSets = new int[2];
                ByteBuffer buffer = ByteBuffer.allocate(8);
                for (int k = 0; k < 8; k++) {
                    buffer.put(reader.readByte());
                }
                
                for (int k = 0; k < 2; k++) {
                    offSets[k] = buffer.getInt(4*k);
                }
                
                int length = offSets[0] + 2 * (offSets[1] - offSets[0]) + 4 * (compressedHeight -offSets[1]);
                
                byte[] read = new byte[length];
                reader.read(read);
                buffer = ByteBuffer.allocate(length);
                buffer.put(read);
                
                buffer.position(0);
                for (int k = 0; k < offSets[0]; k++) {
                    transformData[i][j][k] = (int) buffer.get();
                }
                for (int k = offSets[0]; k < offSets[1]; k++) {
                    transformData[i][j][k] = (int) buffer.getShort();
                }
                for (int k = offSets[1]; k < compressedHeight; k++) {
                    transformData[i][j][k] = (int) buffer.getInt();
                }
                
            }
        }

        reader.close();
        return transformData;

    }

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
     * short data types. If no coefficient requires these data types, the
     * offsets will be the length of the data array.
     *
     * @param data A line of the compressed data.
     * @return The offsets of shorts and integers respectively.
     */
    private static int[] calculateOffsets(int[] data) {

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
    
    // KOKEILU BUFFERILLA:
    
    private static byte[] createLineOfMixedData(int[] data){
        
        
        int[] offSets = calculateOffsets(data);

        int noOfBytes = offSets[0];
        int noOfShorts = offSets[1] - offSets[0];
        int noOfInts = data.length - offSets[1];
        int length = 8 + noOfBytes + 2 * noOfShorts + 4 * noOfInts;
        
        ByteBuffer buffer = ByteBuffer.allocate(length);
        
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

    private static byte[] createLineOfMixedDataOriginal(int[] data) {

        int[] offSets = calculateOffsets(data);

        int noOfBytes = offSets[0];
        int noOfShorts = offSets[1] - offSets[0];
        int noOfInts = data.length - offSets[1];
        int length = noOfBytes + 2 * noOfShorts + 4 * noOfInts;

        byte[] newData = new byte[length + 8];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                newData[4 * i + j] = (byte)  (offSets[i] >> (8*j));
            }
        }

        int pointer = 8;

        for (int i = 0; i < noOfBytes; i++) {
            newData[pointer + i] = (byte) data[i];
        }

        pointer += noOfBytes;
        for (int i = 0; i < noOfShorts; i++) {
            for (int j = 0; j < 2; j++) {
                newData[pointer + 2 * i + j] = (byte) (data[noOfBytes + i] >> (8*j));
            }
        }

        pointer += noOfShorts;
        for (int i = 0; i < noOfInts; i++) {
            for (int j = 0; j < 4; j++) {
                newData[pointer + 4 * i + j] = (byte) (data[pointer - 8 + i] >> (8*j));
            }
        }

        return newData;
    }
    
    private static byte[] transformToBytes(int[] data) {
        byte[] b = new byte[data.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) data[i];
        }
        return b;
    }

    private static short[] transformToShorts(int[] data) {
        short[] s = new short[data.length];
        for (int i = 0; i < s.length; i++) {
            s[i] = (short) data[i];
        }
        return s;
    }
    
    private static int byteToInt(byte b){
        if (b < 0) {
            return 256 + b;
        }
        return (int) b;
    }
}
