package compression;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

public class BitmapIOTest {

    boolean TEST_FILE_PRESENT = false;
    // Switch true if there's a testfile "c.bmp" in the main folder.

    @Test
    public void testBgrToColor_someColorIsExtracted() {

        byte extracted;
        int composite;
        for (int color = 0; color < 3; color++) {
            for (int i = 0; i < 256; i++) {
                composite = i << (8 * color);
                extracted = BitmapIO.bgrIntegerToColor(composite, color);
                assertTrue("Color not extracted", extracted != 0 || i == 128);
            }
        }
    }

    @Test
    public void testBgrToColor_noSpillingToOtherColors() {

        // Test that single basic color won't spill into other basic colors.

        byte extracted;
        int composite;

        for (int color = 0; color < 3; color++) {
            for (int i = 0; i < 256; i++) {
                composite = i << (8 * color);
                for (int otherColor = 0; otherColor < 3; otherColor++) {
                    if (otherColor == color) {
                        ;
                    } else {
                        extracted = BitmapIO.bgrIntegerToColor(composite, otherColor);
                        assertTrue("Basic colors mix", extracted == - 128); // - 128 ~ 0 shade of the color.
                    }
                }
            }
        }
    }

    @Test
    public void testBgrToColor_ShadesInCorrectOrder() {

        // Test that the shades will come in the correct order (-128,....,127)
        byte extracted;
        int composite;
        for (int color = 0; color < 3; color++) {
            int previous = -129;
            for (int i = 0; i < 256; i++) {
                composite = i << (8 * color);
                extracted = BitmapIO.bgrIntegerToColor(composite, color);
                assertTrue("Shades in wrong order. Color: " + color + " shade: " + extracted, extracted - previous == 1);
                previous = extracted;
            }
        }
    }

    @Test
    public void testColorToBgr_noSpillingWhenCombining() {

        // Test that single basic color results an integer whose noncorresponding
        // bytes are zero:

        byte[] data = new byte[3];
        int mask;   // a bitmask to disregard the color that should be present in 
        // the integer.
        for (int color = 0; color < 3; color++) {
            // set the other colors to the shade zero (-128 in byte language):
            // create the bitmask
            mask = 0;
            for (int i = 0; i < 3; i++) {
                if (i != color) {
                    data[i] = -128;
                    mask += 0xFF << (i * 8);
                }
            }
            // Set the color to different shades:
            for (int i = -128; i < 128; i++) {
                data[color] = (byte) i;
                int composite = BitmapIO.colorsToBgrInteger(data);
                composite = composite & mask;
                assertTrue("Some basic color spills into other colors", composite == 0);
            }
        }
    }

    @Test
    public void testColorToBgr_noColorsVanish() {

        // Test that nonzero shades will become nonzero shades:

        byte[] data = new byte[3];
        for (int color = 0; color < 3; color++) {
            for (int i = 0; i < 3; i++) {
                data[i] = -128; // set the shades to zero
            }

            for (int i = -127; i < 128; i++) {
                data[color] = (byte) i;
                int composite = BitmapIO.colorsToBgrInteger(data);
                assertTrue("Some color results a 'zero color'.", composite != 0);
            }
        }
    }

    @Test
    public void testFromIntegerToByteAndBackAgain_BigValues() {
        // Test 1000 biggest values:

        int original, result;
        byte[] byteData = new byte[3];


        for (int i = 0; i < 1000; i++) {
            original = 0xFF0000 - i;
            for (int color = 0; color < 3; color++) {
                byteData[color] = BitmapIO.bgrIntegerToColor(original, color);
            }
            result = BitmapIO.colorsToBgrInteger(byteData);

            assertTrue("Value " + Integer.toHexString(original) + " doesn't map right int->byte->int", result == original);
        }
    }

    @Test
    public void testFromIntegerToByteAndBackAgain_SmallValues() {


        // Test 1000 smallest values:
        int original, result;
        byte[] byteData = new byte[3];

        for (int i = 0; i < 1000; i++) {
            original = i;
            for (int color = 0; color < 3; color++) {
                byteData[color] = BitmapIO.bgrIntegerToColor(original, color);
            }
            result = BitmapIO.colorsToBgrInteger(byteData);

            assertTrue("Value " + Integer.toHexString(original) + " doesn't map right int->byte->int", result == original);
        }
    }

    @Test
    public void testFromIntegerToByteAndBackAgain_RandomValues() {


        // Test 1000 random numbers:

        Random random = new Random();
        int original, result;
        byte[] byteData = new byte[3];

        for (int i = 0; i < 1000; i++) {
            original = random.nextInt(0x1000000);
            for (int color = 0; color < 3; color++) {
                byteData[color] = BitmapIO.bgrIntegerToColor(original, color);
            }
            result = BitmapIO.colorsToBgrInteger(byteData);

            assertTrue("Value " + Integer.toHexString(original) + " doesn't map right int->byte->int", result == original);
        }
    }

    @Test
    public void testFromIntegerToByteAndBackAgain_ExtraDataVanishes() {

        // Test 1000 random numbers with the possibility of extra information
        // that has to be lost:

        Random random = new Random();
        int original, result;
        byte[] byteData = new byte[3];
        int mask = 0xFFFFFF;
        for (int i = 0; i < 1000; i++) {
            original = random.nextInt();
            for (int color = 0; color < 3; color++) {
                byteData[color] = BitmapIO.bgrIntegerToColor(original, color);
            }
            result = BitmapIO.colorsToBgrInteger(byteData);

            assertTrue("Value " + Integer.toHexString(original) + " doesn't map right int->byte->int", result == (mask & original));
        }
    }

    @Test
    public void testReadTransformAndWrite() {
        // Reads and writes a file, then compares the original to the new one.
        if (TEST_FILE_PRESENT) {
            try {
                File f = new File("DESTROYTHIS.bmp");
                byte[][][] data = BitmapIO.readFileIntoByteData(new File("c.bmp"));
                BitmapIO.writeByteDataIntoBitmap(data, f);
                byte[][][] newData = BitmapIO.readFileIntoByteData(f);
                assertEquals("New picture has wrong dimensions", newData.length, data.length);
                assertEquals("New picture has wrong dimensions", newData[0].length, data[0].length);
                assertEquals("New picture has wrong dimensions", newData[0][0].length, data[0][0].length);

                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < data[0].length; j++) {
                        for (int k = 0; k < data[0][0].length; k++) {
                            if (data[i][j][k] != newData[i][j][k]) {
                                assertTrue("Data changes when reading or writing.", false);
                                break;
                            }
                        }
                    }
                }

                f.delete();
            } catch (IOException ex) {
                assertTrue("Reading failed", false);
            }

        }

    }
}
