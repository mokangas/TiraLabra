/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compression;

import java.io.File;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hamppis
 */
public class BitmapIOTest {
    
    public BitmapIOTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

 /**
     * Test of bgrIntegerToColor method, of class BitmapIO.
     */
    @Test
    public void testBgrIntegerToColor() {

        // Test that the color will be extracted:
        byte extracted;
        int composite;
        for (int color = 0; color < 3; color++) {
            for (int i = 0; i < 256; i++) {
                composite = i << (8 * color);
                extracted = BitmapIO.bgrIntegerToColor(composite, color);
                assertTrue("Color not extracted", extracted != 0 || i == 128);
            }
        }

        // Test that single basic color won't spill into other basic colors.
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


        // Test that the shades will come in the correct order (-128,....,127)
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

    /**
     * Test of colorsToBgrInteger method, of class BitmapIO.
     */
    @Test
    public void testColorsToBgrInteger() {

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


        // Test that nonzero shades will become nonzero shades:
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
    public void testFromIntegerToByteAndBack() {
        int original, result;
        byte[] byteData = new byte[3];

        // Test 1000 biggest values:

        for (int i = 0; i < 1000; i++) {
            original = 0xFF0000 - i;
            for (int color = 0; color < 3; color++) {
                byteData[color] = BitmapIO.bgrIntegerToColor(original, color);
            }
            result = BitmapIO.colorsToBgrInteger(byteData);

            assertTrue("Value " + Integer.toHexString(original) + " doesn't map right int->byte->int", result == original);
        }

        // Test 1000 smallest values:

        for (int i = 0; i < 1000; i++) {
            original = i;
            for (int color = 0; color < 3; color++) {
                byteData[color] = BitmapIO.bgrIntegerToColor(original, color);
            }
            result = BitmapIO.colorsToBgrInteger(byteData);

            assertTrue("Value " + Integer.toHexString(original) + " doesn't map right int->byte->int", result == original);
        }
        
        // Test 1000 random numbers:
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            original = random.nextInt(0x1000000);
            for (int color = 0; color < 3; color++) {
                byteData[color] = BitmapIO.bgrIntegerToColor(original, color);
            }
            result = BitmapIO.colorsToBgrInteger(byteData);

            assertTrue("Value " + Integer.toHexString(original) + " doesn't map right int->byte->int", result == original);
        }
        
        
        // Test 1000 random numbers with the possibility of extra information
        // that has to be lost:
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
}
