/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compression;

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
public class AuxiliaryMethodsTest {
    
    public AuxiliaryMethodsTest() {
    }
  

    /**
     * Test of bgrIntegerToColor method, of class AuxiliaryMethods.
     */
    @Test
    public void testBgrIntegerToColor() {
        byte b = 1;
        int i = 1;
        byte result = AuxiliaryMethods.bgrIntegerToColor(i,0);
        assertEquals("Number 1 should transform into 1 (when the color's blue). ",b,result);
        
        for (int j = 1; j < 256; j++) {
            byte blue = AuxiliaryMethods.bgrIntegerToColor(j, 0);
            assertTrue("Numbers 1-255 should have nonzero blue component", blue != 0);
            
            byte green = AuxiliaryMethods.bgrIntegerToColor(j, 1);
            assertTrue("Numbers 1-255 should have zero green component", green == 0);
            
            byte red = AuxiliaryMethods.bgrIntegerToColor(j, 2);
            assertTrue("Numbers 1-255 should have zero red component", red == 0);
        }
        
        for (int j = 0; j < 255; j++) {
            int a = j << 8;
            byte blue = AuxiliaryMethods.bgrIntegerToColor(a, 0);
            assertTrue("Numbers 0-255<<8 should have zero blue component", blue == 0);
            
            byte green = AuxiliaryMethods.bgrIntegerToColor(a, 1);
            assertTrue("Numbers 0-255<<8 should have nonzero green component", green != 0);
            
            byte red = AuxiliaryMethods.bgrIntegerToColor(a, 2);
            assertTrue("Numbers 0-255<<8 should have zero red component", red == 0);
        }
    }

    /**
     * Test of colorsToBgrInteger method, of class AuxiliaryMethods.
     */
    @Test
    public void testColorsToBgrInteger() {
    }
}
