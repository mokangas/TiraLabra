package compression;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class WTFIOTest {

    boolean TEST_FILE_PRESENT = false;
    // Switch true if there's a testfile "c.bmp" in the main folder.
    
    byte[][][] origData;
    WTFIO testWtf;
    WTFIO testWtfLossless;
    int typeOfFile;
    int lol;
    int noOfColors;
    int origWidth;
    int origHeight;
    File testFile;
    File testFileLossless;

    public WTFIOTest() {
    }

    @Before
    public void setUp() throws FileNotFoundException, IOException {

        if (TEST_FILE_PRESENT) {

            origData = BitmapIO.readFileIntoByteData(new File("c.bmp"));
            lol = 3;
            typeOfFile = 1;
            noOfColors = origData.length;
            origWidth = origData[0].length;
            origHeight = origData[0][0].length;

            int[][][] transform = HaarTransform.lossyTransfrom(origData, lol);
            testFile = new File("testFile.wtf");
            WTFIO.writeMixedData(transform, origHeight, lol, testFile);
            testWtf = new WTFIO(testFile);

            int[][][] losslessTransform = HaarTransform.lossyTransfrom(origData, 0);
            testFileLossless = new File("testFileLossless.wtf");
            WTFIO.writeMixedData(losslessTransform, origHeight, 0, testFileLossless);
            testWtfLossless = new WTFIO(testFileLossless);
        }
    }

    @After
    public void tearDown() {
        if (TEST_FILE_PRESENT) {
            testFile.delete();
            testFileLossless.delete();
        }
    }

    @Test
    public void calculateOffsets_onlyBytes() {

        int[] a = {0, 2, 2, 0, 3,};
        int[] offSets = WTFIO.calculateOffsets(a);

        assertEquals("Short offset wrong", a.length, offSets[0]);
        assertEquals("Int offset wrong", a.length, offSets[1]);

    }

    @Test
    public void calculateOffsets_maxAndMinBytes() {

        int[] a = {0, 2, 2, Byte.MAX_VALUE, Byte.MIN_VALUE, 0, 3,};
        int[] offSets = WTFIO.calculateOffsets(a);

        assertEquals("Short offset wrong", a.length, offSets[0]);
        assertEquals("Int offset wrong", a.length, offSets[1]);

    }

    @Test
    public void calculateOffsets_containsShorts() {

        int[] a = {0, 2, 2, 300, 3,};
        int[] offSets = WTFIO.calculateOffsets(a);

        assertEquals("Short offset wrong", 3, offSets[0]);
        assertEquals("Int offset wrong", a.length, offSets[1]);

    }

    @Test
    public void calculateOffsets_minMaxShorts() {

        int[] a = {0, 2, 2, Short.MAX_VALUE, Short.MIN_VALUE, 0, 3,};
        int[] offSets = WTFIO.calculateOffsets(a);

        assertEquals("Short offset wrong", 3, offSets[0]);
        assertEquals("Int offset wrong", a.length, offSets[1]);

    }

    @Test
    public void calculateOffsets_containsInts() {

        int[] a = {0, 2, 2, 40000, 3,};
        int[] offSets = WTFIO.calculateOffsets(a);

        assertEquals("Short offset wrong", 3, offSets[0]);
        assertEquals("Int offset wrong", 3, offSets[1]);

    }

    @Test
    public void calculateOffsets_shortOffsetIsSmallerThanIntOffset() {

        int[] a = {0, 2, 40000, 300, 3,};
        int[] offSets = WTFIO.calculateOffsets(a);

        assertTrue("Short offset is bigger than int offset.", offSets[0] <= offSets[1]);

    }

    @Test
    public void createLineOfMixedData_onlyBytes() {
        int[] a = {0, 2, 2, 0, 3,};
        byte[] b = WTFIO.createLineOfMixedData(a);

        assertEquals("Not enough bytes on a line of  mixed data", b.length, a.length + 8);

        ByteBuffer buff = ByteBuffer.allocate(b.length);
        buff.put(b);

        assertEquals("Short offset wrong", buff.getInt(0), a.length);
        assertEquals("Int offset wrong", buff.getInt(4), a.length);

        for (int i = 0; i < a.length; i++) {
            assertEquals("Byte written wrong on a line of mixed data.", a[i], buff.get(8 + i));
        }
    }

    @Test
    public void createLineOfMixedData_bigAndSmallBytes() {

        int[] a = {1, 1, Byte.MAX_VALUE, Byte.MIN_VALUE, 2, 2};
        byte[] b = WTFIO.createLineOfMixedData(a);

        assertEquals("Not enough bytes on a line of  mixed data", b.length, a.length + 8);

        ByteBuffer buff = ByteBuffer.allocate(b.length);
        buff.put(b);

        assertEquals("Short offset wrong with big/small bytes", buff.getInt(0), a.length);

        buff.position(8); // after the offsets
        for (int i = 0; i < a.length; i++) {
            assertEquals("Some bytes written wrong", a[i], buff.get());
        }

    }

    @Test
    public void createLineOfMixedData_containsShorts() {

        int[] a = {0, -5, 1111, 3};
        byte[] b = WTFIO.createLineOfMixedData(a);

        assertEquals("Not enough bytes on a line of  mixed data", b.length, a.length + 8 + 2); // +2 because of the shorts at the end

        ByteBuffer buff = ByteBuffer.allocate(b.length);
        buff.put(b);

        assertEquals("Short offset wrong.", 2, buff.getInt(0));

        buff.position(8);
        assertEquals("Byte written wrong", a[0], buff.get());
        assertEquals("Byte written wrong", a[1], buff.get());
        assertEquals("Short written wrong", a[2], buff.getShort());
        assertEquals("Short written wrong", a[3], buff.getShort());

    }

    @Test
    public void createLineOfMixedData_bigAndSMallShorts() {
        int[] a = {0, -5, Short.MAX_VALUE, Short.MIN_VALUE, 3};
        byte[] b = WTFIO.createLineOfMixedData(a);

        assertEquals("Not enough bytes on a line of  mixed data", b.length, a.length + 8 + 3); // +3 because of the shorts at the end

        ByteBuffer buff = ByteBuffer.allocate(b.length);
        buff.put(b);

        assertEquals("Short offset wrong.", 2, buff.getInt(0));
        assertEquals("Max/min short got written as an int.", a.length, buff.getInt(4));

        buff.position(8);
        assertEquals("Byte written wrong", a[0], buff.get());
        assertEquals("Byte written wrong", a[1], buff.get());
        assertEquals("Short written wrong", a[2], buff.getShort());
        assertEquals("Short written wrong", a[3], buff.getShort());
        assertEquals("Short written wrong", a[4], buff.getShort());

    }

    @Test
    public void testFileHeadert() {
        if (TEST_FILE_PRESENT) {

            assertEquals("File type written/read wrong.", testWtf.getTypeOfFile(), typeOfFile);
            assertEquals("Level of loss written/read wrong.", testWtf.getLevelOfLoss(), lol);
            assertEquals("Number of colors written/raed wrong.", testWtf.getNoOfColors(), noOfColors);
            assertEquals("Original height written/read wrong.", testWtf.getOriginalHeight(), origHeight);
            assertEquals("Original width written/raed wrong.", testWtf.getOriginalWidth(), origWidth);

            assertEquals("Level of loss written/read wrong", testWtfLossless.getLevelOfLoss(), 0);
        }
    }

    @Test
    public void testLosslessCompressionDoesntLoseDataAndIsWrittenAndReadRight() throws IOException {
        if (TEST_FILE_PRESENT) {
            int[][][] transform = testWtfLossless.readData();
            byte[][][] retrieved = HaarTransform.inverseLossyTransform(transform, origHeight, 0);
            assertTrue("Lossless transform loses data or is written/read incorrectly.", areTheSameArrays(origData, retrieved));
        }
    }

    public static boolean areTheSameArrays(byte[][][] a, byte[][][] b) {
        if (a.length != b.length) {
            return false;
        }

        if (a[0].length != b[0].length) {
            return false;
        }

        if (a[0][0].length != b[0][0].length) {
            return false;
        }

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                for (int k = 0; k < a[0][0].length; k++) {
                    if (a[i][j][k] != b[i][j][k]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
