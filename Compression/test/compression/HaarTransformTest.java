package compression;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class HaarTransformTest {

    public static boolean TEST_FILE_PRESENT = false; // switch true if there's a test file
    // "c.bmp" in the folder.
    
    Random random;

    @Before
    public void setUp() {
        random = new Random();
    }

    @Test
    public void testSupPowerOfTwo_someValues() {
        int[] sups = new int[100];
        for (int i = 0; i < sups.length; i++) {
            sups[i] = HaarTransform.supPowerOfTwo(i);
        }

        assertEquals(sups[1], 1);
        assertEquals(sups[2], 2);
        assertEquals(sups[3], 2);
        assertEquals(sups[4], 4);
        assertEquals(sups[63], 32);
        assertEquals(sups[64], 64);
        assertEquals(sups[65], 64);
        assertEquals(sups[99], 64);

        assertEquals(HaarTransform.supPowerOfTwo(1000), 512);
    }

    @Test
    public void testSupPowerOfTwo_forPowersOfTwo() {
        for (int i = 0; i < 21; i++) {
            int p = HaarTransform.pow(2, i);
            assertEquals("Pow or supPowerOfTwo is broken.", p, HaarTransform.supPowerOfTwo(p));
        }
    }

    // This test may pass also with extreme luck.
    @Test
    public void testCopyValuesTo() {
        byte[] copyHere = new byte[20];
        byte[] origArray = new byte[20];
        byte[] copyThese = new byte[10];

        for (int i = 0; i < 20; i++) {
            copyHere[i] = (byte) random.nextInt();
            origArray[i] = copyHere[i];
        }
        for (int i = 0; i < 10; i++) {
            copyThese[i] = (byte) random.nextInt();
        }

        HaarTransform.copyValuesTo(copyThese, copyHere, 5);

        for (int i = 0; i < 5; i++) {
            assertEquals(origArray[i], copyHere[i]);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(copyHere[5 + i], copyThese[i]);
        }
        for (int i = 15; i < 20; i++) {
            assertEquals(origArray[i], copyHere[i]);
        }
    }

    @Test
    public void testJoinArrays() {
        int[] a = new int[10];
        int[] b = new int[10];
        for (int i = 0; i < 10; i++) {
            a[i] = random.nextInt();
            b[i] = random.nextInt();
        }

        int[] c = HaarTransform.joinArrays(a, b);
        assertEquals(c.length, a.length + b.length);

        for (int i = 0; i < 10; i++) {
            assertEquals(a[i], c[i]);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(b[i], c[10 + i]);
        }
    }

    @Test
    public void testPow_random() {
        for (int i = 0; i < 20; i++) {
            int power = 1 + random.nextInt(9);
            int a = random.nextInt(10);
            assertEquals(HaarTransform.pow(a, power), (int) Math.pow(a, power));
        }

    }

    @Test
    public void testPow_powersOfTwo() {

        for (int i = 1; i < 21; i++) {
            assertEquals(HaarTransform.pow(2, i), (int) Math.pow(2, i));
        }
    }

    @Test
    public void testAddDivideConvert() {
        int[] a = {5, 12, 26, 208, 33};
        byte[] b = HaarTransform.addDivideConvert(a, 2, 7);

        assertEquals(b[0], 1);
        assertEquals(b[1], 2);
        assertEquals(b[2], 4);
        assertEquals(b[3], 30);
        assertEquals(b[4], 5);
    }

    @Test
    public void testAddDivideConvert2() {
        int howMany = Byte.MAX_VALUE / 11;
        int[] a = new int[howMany];
        for (int i = 0; i < howMany; i++) {
            a[i] = i * 11 - 7;
        }

        byte[] b = HaarTransform.addDivideConvert(a, 7, 11);
        for (int i = 0; i < b.length; i++) {
            assertEquals(i, b[i]);
        }
    }

    @Test
    public void testAddDivideConvert_overflow() {
        int[] a = {1, 251};
        byte[] b = HaarTransform.addDivideConvert(a, 9, 10);
        assertEquals(1, b[0]);
        assertEquals(26, b[1]);

    }

    @Test
    public void sumTree_rightSize() {
        byte[] b = new byte[32];
        for (int i = 0; i < 32; i++) {
            b[i] = (byte) random.nextInt();
        }

        int[] a = HaarTransform.sumTree(b, 1);
        assertEquals(a.length, 63);

        a = HaarTransform.sumTree(b, 4);
        assertEquals(a.length, 15);
    }

    @Test
    public void sumTree_isSumTree() {
        for (int arraySize = 0; arraySize < 10; arraySize++) {
            byte[] b = new byte[HaarTransform.pow(2, arraySize)];
            int[] a = HaarTransform.sumTree(b, 1);
            assertTrue(isSumTree(a));
            if (arraySize > 3) {
                a = HaarTransform.sumTree(b, 4);
                assertTrue(isSumTree(a));
            }
        }
    }

    public boolean isSumTree(int[] a) {

        int belowLength = a.length / 2 + 1;
        int pointer = 0;
        while (belowLength >= 2) {
            int[] below = Arrays.copyOfRange(a, pointer, pointer + belowLength);
            int[] above = Arrays.copyOfRange(a, pointer + belowLength, pointer + belowLength + belowLength / 2);
            if (!isBelowLevelInSumTree(below, above)) {
                return false;
            }
            pointer += belowLength;
            belowLength /= 2;
        }


        return true;
    }

    public boolean isBelowLevelInSumTree(int[] below, int[] above) {

        if (2 * above.length != below.length) {
            return false;
        }

        for (int i = 0; i < above.length; i++) {
            if (above[i] != below[2 * i] + below[2 * i + 1]) {
                return false;
            }
        }

        return true;
    }

    @Test
    public void testPowOfTwo_TenRandomData() {
        for (int i = 0; i < 10; i++) {

            byte[] data = new byte[1024];
            for (int j = 0; j < data.length; j++) {
                data[j] = (byte) random.nextInt();
            }

            int[] transform = HaarTransform.lossyTransformPowerOfTwo(data, 0);
            byte[] newData = HaarTransform.inverseLossyPowerOfTwo(transform, 0);
            assertTrue(areTheSameArray(data, newData));
        }
    }

    public void testArbitraryLength_TenRandomData() {
        for (int i = 0; i < 10; i++) {
            int length = 1 + random.nextInt(2000);
            byte[] data = new byte[length];
            for (int j = 0; j < data.length; j++) {
                data[j] = (byte) random.nextInt();
            }

            int[] transform = HaarTransform.lossyTransformPowerOfTwo(data, 0);
            byte[] newData = HaarTransform.inverseLossyPowerOfTwo(transform, 0);
            assertTrue(areTheSameArray(data, newData));
        }
    }

    public void testTransformLengthOne() {
        for (int i = -1; i < 2; i++) {
            byte[] b = new byte[1];
            b[0] = (byte) (4 * i);
            int[] a = HaarTransform.lossyTransformArbitraryLength(b, 0);
            byte[] newB = HaarTransform.inverseLossyArbitraryLength(a, 1, 0);
            assertEquals(newB, 4 * i);
        }
    }

    public static boolean areTheSameArray(byte[] a, byte[] b) {
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

    @Test
    public void testTransformAndInverse_handMadeArray() {

        int d0 = 4;
        int d1 = 5;
        int d2 = 6;
        byte[][][] data = new byte[d0][d1][d2];

        for (int i = 0; i < d0; i++) {
            for (int j = 0; j < d1; j++) {
                for (int k = 0; k < d2; k++) {
                    data[i][j][k] = (byte) (i - j + k);
                }
            }
        }

        int[][][] transform = HaarTransform.lossyTransfrom(data, 0);
        byte[][][] retrieved = HaarTransform.inverseLossyTransform(transform, d2, 0);
        assertTrue(WTFIOTest.areTheSameArrays(data, retrieved));
    }

    @Test
    public void testTransformAndInverse_tenRandom3DArrays() {
        for (int i = 0; i < 10; i++) {
            int d0 = 1 + random.nextInt(10);
            int d1 = 1 + random.nextInt(3000);
            int d2 = 1 + random.nextInt(3000);

            byte[][][] data = new byte[d0][d1][d2];
            for (int j = 0; j < d0; j++) {
                for (int k = 0; k < d1; k++) {
                    for (int l = 0; l < d2; l++) {
                        data[j][k][l] = (byte) random.nextInt();
                    }
                }
            }

            int[][][] transform = HaarTransform.lossyTransfrom(data, 0);
            byte[][][] retrieved = HaarTransform.inverseLossyTransform(transform, d2, 0);
            assertTrue(WTFIOTest.areTheSameArrays(data, retrieved));
        }
    }
    
    @Test
    public void testTransfromAndInverse_ImageData() throws IOException {
        
        byte[][][] imageData = BitmapIO.readFileIntoByteData(new File("c.bmp"));
        int[][][] transform = HaarTransform.lossyTransfrom(imageData, 0);
        byte[][][] retrieved = HaarTransform.inverseLossyTransform(transform, imageData[0][0].length, 0);
        
        assertTrue(WTFIOTest.areTheSameArrays(imageData, retrieved));
    }
}
