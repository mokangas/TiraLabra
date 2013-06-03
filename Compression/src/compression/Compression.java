package compression;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Compression {

    /**
     * Everything here is just for testing.
     */
    public static void main(String[] args) throws IOException {

        File file = new File("c1.bmp");
        testSix(file);
    }

    /**
     * Will be deleted.
     */
    public static void testOne(File file) throws IOException {
        byte[][][] data = BitmapIO.readFileIntoByteData(file);
        BitmapIO.writeByteDataIntoBitmap(data, new File("d.bmp"));
    }

    /**
     * Will be deleted
     */
    public static void testTwo(File file) throws IOException {
        byte[][][] data = BitmapIO.readFileIntoByteData(file);
        byte[] d = new byte[1024];
        for (int i = 0; i < d.length; i++) {
            d[i] = data[0][100][i];
        }
        int[] transform = HaarTransform.transformPowerOfTwo(d);
        print(d, transform, HaarTransform.inversePowerOfTwo(transform));
    }

    /**
     * Will be deleted
     */
    public static void testThree(File file) throws IOException {
        byte[][][] data = BitmapIO.readFileIntoByteData(file);
        byte[] d = new byte[1000];
        for (int i = 0; i < d.length; i++) {
            d[i] = data[0][100][i];
        }
        int[] transform = HaarTransform.transformArbitraryLength(d);
        print(d, transform, HaarTransform.inverseArbitraryLength(transform));

    }

    /**
     * Will be deleted
     */
    public static void testFour(File file) throws IOException {
        byte[][][] data = BitmapIO.readFileIntoByteData(file);
        Random r = new Random();

        for (int row = 0; row < 100; row++) {
            int howMany = 2 + r.nextInt(data[0][0].length - 2);
            byte[] d = new byte[howMany];
            for (int i = 0; i < d.length; i++) {
                d[i] = data[0][row][i];
            }
            int[] transform = HaarTransform.transformArbitraryLength(d);
            byte[] e = HaarTransform.inverseArbitraryLength(transform);
            if (!compareArrays(d,e)) {
                System.out.println("Doesn't work!");
            }
        }
    }

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

    /**
     * Will be delted.
     */
    private static void testFive(File file) {
        for (int i = 0; i < 100; i++) {
            System.out.println(i+"\t"+HaarTransform.supPowerOfTwo(i));
        }
    }
    
    /**
     * Will be delted.
     */
    private static void testSix(File file) throws IOException {
        byte[][][] data = BitmapIO.readFileIntoByteData(file);
        int[][][] transform = HaarTransform.transform3DArray(data);
        byte[][][] retrieved = HaarTransform.inverse3DArray(transform);
        
        BitmapIO.writeByteDataIntoBitmap(data, new File("test.bmp"));
    }
}
