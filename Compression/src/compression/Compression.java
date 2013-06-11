package compression;

import GUI.GraphicalUI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class Compression {

    public static void main(String[] args) throws IOException {

//        koe();

        switch (args.length) {
            case 0:
                launchGui();
                break;
            case 2:
                commandLineFromWtfToBmp(args);
                break;
            case 3:
                commandLineFromBmpToWtf(args);
                break;
            case 4:
                commandLineSeries(args);
                break;
            default:
                System.out.println("Syntax error.");
        }
    }

    public static void koe() {
    }

    /**
     * If the program is run from the command line and has three arguments, this
     * method will be called. It transforms a bmp-file to a wtf-file.
     *
     * @param args[0] level of loss of the transform, 0 is lossless.
     * @param args[1] The (path and) name of the bmp file to be tranformed.
     * @param args[2] The (path and) name of the wtf file to which the
     * tranformed data will be saved.
     * @throws IOException
     */
    public static void commandLineFromBmpToWtf(String[] args) throws IOException {
        long time = System.currentTimeMillis();
        int levelOfLoss = Integer.parseInt(args[0]);
        File inputFile = new File(args[1]);
        File outputFile = new File(args[2]);

        System.out.println("Reading data...");
        byte[][][] data = BitmapIO.readFileIntoByteData(inputFile);
        int originalHeight = data[0][0].length; // This hopefully lets the garbage collector destroy the data array.
        System.out.println("Calculating transform...");
        int[][][] transform = HaarTransform.lossyTransfrom(data, levelOfLoss);
        System.out.println("Writing to file " + args[2] + " ...");
        WTFIO.writeMixedData(transform, originalHeight, levelOfLoss, outputFile);

        time = System.currentTimeMillis() - time;
        System.out.println("Ready. Duration " + time + " milliseconds.");
    }

    public static void commandLineSeries(String[] args) throws IOException {
        int lowest = Integer.parseInt(args[0]);
        int highest = Integer.parseInt(args[1]);

        for (int i = lowest; i <= highest; i++) {
            String[] parameters = {"" + i, args[2], args[3] + i + ".wtf"};
            commandLineFromBmpToWtf(parameters);
        }
    }

    /**
     * If the program is run from the command line and has three arguments, this
     * mehtod will be called. It transfroms a wtf-file to a bmp-file.
     *
     * @param args[0] The wtf-file to be transformed.
     * @param args[1] The resulting bmp-file.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void commandLineFromWtfToBmp(String[] args) throws FileNotFoundException, IOException {
        File inputFile = new File(args[0]);
        File outputFile = new File(args[1]);

        WTFIO read = new WTFIO(inputFile);
        int[][][] transform = read.readData();
        int originalHeight = read.getOriginalHeight();  // This is to let garbage collector to...
        int levelOfLoss = read.getLevelOfLoss();        // ..destroy the object read.
        byte[][][] data = HaarTransform.inverseLossyTransform(transform, originalHeight, levelOfLoss);
        BitmapIO.writeByteDataIntoBitmap(data, outputFile);
    }
    
    /**
     * Launches the GUI.
     */
    private static void launchGui() {
        GraphicalUI gui = new GraphicalUI();
    }
    
    
    

    // THINGS BELOW THIS ARE FOR TESTING PURPOSES AND WILL BE DELETED.
    public static boolean compareArrays(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }

        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Will be deleted.
     */
    private static void printData(int[] data) {
        for (int i = 0; i < data.length; i++) {
            System.out.print(data[i] + " ");
        }
        System.out.println("");
    }

    /**
     * Will be deleted.
     */
    private static void printData(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            System.out.print(data[i] + " ");
        }
        System.out.println("");
    }

    /**
     * Will be deleted.
     */
    private static void print(byte[] originalData, int[] transform, byte[] retrievedData) {
        System.out.print("Original data: \t\t");
        printData(originalData);
        System.out.print("The transform: \t\t");
        printData(transform);
        System.out.print("Retrieved data: \t");
        printData(retrievedData);
    }

    private static void analyseTransfromRow(int[] data, FileWriter writer) throws IOException {
        int interval = 0;
        int intervalLength = HaarTransform.supPowerOfTwo(data.length) / 2;
        int pointer = 0;

        for (int i = intervalLength; i >= 1; i /= 2) {
            int smallest = Integer.MAX_VALUE;
            int biggest = Integer.MIN_VALUE;
            for (int j = 0; j < i; j++) {
                if (data[pointer + j] > biggest) {
                    biggest = data[pointer + j];
                }
                if (data[pointer + j] < smallest) {
                    smallest = data[pointer + j];
                }
            }
            int dif = biggest - smallest;
            writer.write(" " + dif);
            // writer.write("s: "+smallest+" b: "+biggest);
            pointer += i;
        }
        writer.write("\n");
    }

    private static void testEleven(File file, int levelOfLoss) throws IOException {
        byte[][][] data = BitmapIO.readFileIntoByteData(file);
        int[][][] transform = HaarTransform.lossyTransfrom(data, levelOfLoss);

        FileWriter w = new FileWriter(new File("trnsform.txt"));
        for (int i = 0; i < transform[0][0].length; i++) {
            w.write(transform[0][100][i] + " ");
        }

        w.flush();
        w.close();
    }

    private static void testTwelve(File file, int levelOfLoss) throws IOException {
        byte[][][] data = BitmapIO.readFileIntoByteData(file);
        int[][][] transform = HaarTransform.lossyTransfrom(data, levelOfLoss);
        analyseData(transform, levelOfLoss);
    }

    private static void analyseData(int[][][] t, int levelOfLoss) {

        int halfBytes = 0;
        int bytes = 0;
        int shorts = 0;
        int tqInts = 0;
        for (int i = 0; i < t.length; i++) {
            for (int j = 0; j < t[0].length; j++) {
                for (int k = 0; k < t[0][0].length; k++) {
                    int a = t[i][j][k];
                    if (-8 <= a && a <= 7) {
                        halfBytes++;
                    } else if (-128 <= a && a <= 127) {
                        bytes++;
                    } else if (-32768 <= a && a <= 32767) {
                        shorts++;
                    } else if (- 8388607 <= a && a <= 0xFFFFFF / 2) {
                        tqInts++;
                    } else {
                        System.out.println("Syntax error! " + a);
                    }
                }
            }
        }
        int total = halfBytes + bytes + shorts + tqInts;

        System.out.println("Half bytes: " + halfBytes + " Bytes: " + bytes + " Shorts: " + shorts + " Ints: " + tqInts);
        System.out.println("Total: " + total);
        int size = t.length * t[0].length * t[0][0].length;
        System.out.println("Size: " + size);

        int bitsInOriginal = size * 8 * (int) Math.pow(2.0, (double) levelOfLoss);
        int bits = halfBytes * 4 + bytes * 8 + shorts * 16 + tqInts * 24;
        bits += 2 * total;
        System.out.println("Bits in original: \t" + bitsInOriginal);
        System.out.println("Bits compressed: \t" + bits);

    }

}
