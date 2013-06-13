package compression;

import java.io.File;
import java.io.IOException;

public class TwoDHaar {
    

    static void koe() throws IOException {
//         byte[][][] data = BitmapIO.readFileIntoByteData(new File("nelio.bmp"));
//        byte[][][] d = new byte[3][8][8];
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 8; j++) {
//                for (int k = 0; k < 8; k++) {
//                    d[i][j][k] = data[i][1000+j][1000+k];
//                }
//            }
//        }
//        BitmapIO.writeByteDataIntoBitmap(d, new File("tosiPieniNelio.bmp"));

        byte[][][] data = BitmapIO.readFileIntoByteData(new File("nelio.bmp"));
        HaarTreeLevel[] transform = transform(data, 0);
        byte[][][] newData = inverse(transform, data.length, data[0].length, data[0][0].length);
        BitmapIO.writeByteDataIntoBitmap(newData, new File("KOKEILU.bmp"));
//        printBytes(data[0]);
//        printTreeLevel(transform[0]);
//        printBytes(newData[0]);
    }

    public static SumTreeLevel createSumTree(byte[][] data, int stepSize) {

        SumTreeLevel bottom = new SumTreeLevel(data, stepSize);
        SumTreeLevel current = bottom;

        for (int i = bottom.getSize(); i >= 1; i = i / 2) {
            SumTreeLevel next = new SumTreeLevel(current);
            next.createContent();
            current = next;
        }

        return bottom;
    }

    public static HaarTreeLevel transformSquare(byte[][] data, int levelOfLoss) {

        SumTreeLevel sumTreeBottom = createSumTree(data, (int) pow(2, levelOfLoss));
        HaarTreeLevel haarTreeBottom = new HaarTreeLevel(null);
        haarTreeBottom.createContent(sumTreeBottom);
        
        HaarTreeLevel current = haarTreeBottom;
        SumTreeLevel sumCurrent = (SumTreeLevel) sumTreeBottom.getAbove();

        while (sumCurrent != null && sumCurrent.getSize() != 0) {
            HaarTreeLevel next = new HaarTreeLevel(current);
            next.createContent(sumCurrent);
            current = next;
            sumCurrent = (SumTreeLevel) sumCurrent.getAbove();
        }



        return haarTreeBottom;
    }

    public static HaarTreeLevel[] transform(byte[][][] data, int levelOfLoss) {

        HaarTreeLevel[] transform = new HaarTreeLevel[data.length];
        for (int i = 0; i < transform.length; i++) {
            transform[i] = transformSquare(data[i], levelOfLoss);
        }
        return transform;
    }

    public static byte[][][] inverse(HaarTreeLevel[] htl, int origColors,
            int origWidth, int origHeight) {

        byte[][][] data = new byte[origColors][origWidth][origHeight];
        for (int i = 0; i < origColors; i++) {
            data[i] = inverseSquare(htl[i], origHeight);
        }

        return data;
    }

    public static byte[][] inverseSquare(HaarTreeLevel htl, int originalSize) {

        long[][] inverse = new long[originalSize][originalSize];
        while (htl != null && htl.getSize() >= 1) {
            addLayer(inverse, htl);
            htl = (HaarTreeLevel) htl.getAbove();
        }

        int average = 0; //htl.getContent()[0][0];
        return addDivideCast(inverse, average, pow(originalSize,2));
    }

    public static void addLayer(long[][] data, HaarTreeLevel htl) {
        long[][] coeffs = htl.getContent();
        int stepSize = data.length / (2 * coeffs.length);
        int supportSize = 2 * stepSize;

        for (int x = 0; x < coeffs.length; x++) {
            for (int y = 0; y < coeffs[0].length; y++) {
                addWavelet(data, x, y, coeffs[x][y], stepSize);
            }
        }

    }

    private static void addWavelet(long[][] data, int x, int y, long coeff, int stepSize) {
        
        if (coeff == 0) {
            return;
        }
        
        int howMany = data.length / (2 * stepSize); // how many rows of wavelets
        howMany = howMany * howMany; // how many wavelets in toto.

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                int plusMinus = (i == j) ? 1 : -1; // Whether the coeff is added or subtracted
                int xTopLeft = x * 2 * stepSize + i * stepSize; // Top left of the are where it is added/subtr.
                int yTopLeft = y * 2 * stepSize + j * stepSize;
                for (int k = 0; k < stepSize; k++) {
                    for (int l = 0; l < stepSize; l++) {
                        data[xTopLeft + k][yTopLeft + l] += plusMinus * coeff * howMany;
                    }
                }
            }
        }

    }

    public static byte[][] addDivideCast(long[][] data, int addThis, long divideByThis) {

        byte[][] b = new byte[data.length][data[0].length];

        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                b[i][j] = (byte) ( (data[i][j] + addThis) / divideByThis);
            }
        }
        return b;
    }

    static void printTreeLevel(TreeLevel tl) {

        long[][] c = tl.getContent();


        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < c.length; j++) {
                System.out.print(c[i][j] + " ");
            }
            System.out.println("");
        }
        System.out.println("======================");

        if (tl.above != null) {
            printTreeLevel(tl.getAbove());
        }
    }

    public static void printBytes(byte[][] data) {

        System.out.println("bytebytebyte");
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                System.out.print(data[i][j] + " ");
            }
            System.out.println("");
        }
        System.out.println("bytebytebyte");
    }

    private static long pow(int n, int power) {
        long a = 1;
        for (int i = 0; i < power; i++) {
            a *= n;
        }
        return a;
    }
}
