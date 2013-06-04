
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
public class HaarTransformTest {
    
    public HaarTransformTest() {
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
     * Test of transformPowerOfTwo method, of class HaarTransform.
     */
    @Test
    public void testTransformPowerOfTwo() {
        System.out.println("transformPowerOfTwo");
        byte[] data = null;
        int[] expResult = null;
        int[] result = HaarTransform.transformPowerOfTwo(data);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of inversePowerOfTwo method, of class HaarTransform.
     */
    @Test
    public void testInversePowerOfTwo() {
        System.out.println("inversePowerOfTwo");
        int[] transform = null;
        byte[] expResult = null;
        byte[] result = HaarTransform.inversePowerOfTwo(transform);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addDivideConvert method, of class HaarTransform.
     */
    @Test
    public void testAddDivideConvert() {
        System.out.println("addDivideConvert");
        int[] data = null;
        int addThis = 0;
        int divideByThis = 0;
        byte[] expResult = null;
        byte[] result = HaarTransform.addDivideConvert(data, addThis, divideByThis);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of transformArbitraryLength method, of class HaarTransform.
     */
    @Test
    public void testTransformArbitraryLength() {
        System.out.println("transformArbitraryLength");
        byte[] data = null;
        int[] expResult = null;
        int[] result = HaarTransform.transformArbitraryLength(data);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of inverseArbitraryLength method, of class HaarTransform.
     */
    @Test
    public void testInverseArbitraryLength() {
        System.out.println("inverseArbitraryLength");
        int[] transform = null;
        byte[] expResult = null;
        byte[] result = HaarTransform.inverseArbitraryLength(transform);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of copyValuesTo method, of class HaarTransform.
     */
    @Test
    public void testCopyValuesTo_3args_1() {
        System.out.println("copyValuesTo");
        int[] copyThese = null;
        int[] copyHere = null;
        int startingIndex = 0;
        HaarTransform.copyValuesTo(copyThese, copyHere, startingIndex);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of copyValuesTo method, of class HaarTransform.
     */
    @Test
    public void testCopyValuesTo_3args_2() {
        System.out.println("copyValuesTo");
        byte[] copyThese = null;
        byte[] copyHere = null;
        int startingIndex = 0;
        HaarTransform.copyValuesTo(copyThese, copyHere, startingIndex);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of supPowerOfTwo method, of class HaarTransform.
     */
    @Test
    public void testSupPowerOfTwo() {
        System.out.println("supPowerOfTwo");
        int n = 0;
        int expResult = 0;
        int result = HaarTransform.supPowerOfTwo(n);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of transform3DArray method, of class HaarTransform.
     */
    @Test
    public void testTransform3DArray() {
        System.out.println("transform3DArray");
        byte[][][] data = null;
        int[][][] expResult = null;
        int[][][] result = HaarTransform.transform3DArray(data);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of inverse3DArray method, of class HaarTransform.
     */
    @Test
    public void testInverse3DArray() {
        System.out.println("inverse3DArray");
        int[][][] transform = null;
        byte[][][] expResult = null;
        byte[][][] result = HaarTransform.inverse3DArray(transform);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of lossyTransformArbitraryLength method, of class HaarTransform.
     */
    @Test
    public void testLossyTransformArbitraryLength() {
        System.out.println("lossyTransformArbitraryLength");
        byte[] data = null;
        int levelOfLoss = 0;
        int[] expResult = null;
        int[] result = HaarTransform.lossyTransformArbitraryLength(data, levelOfLoss);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of lossyTransfrom method, of class HaarTransform.
     */
    @Test
    public void testLossyTransfrom() {
        System.out.println("lossyTransfrom");
        byte[][][] data = null;
        int levelOfLoss = 0;
        int[][][] expResult = null;
        int[][][] result = HaarTransform.lossyTransfrom(data, levelOfLoss);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of pow method, of class HaarTransform.
     */
    @Test
    public void testPow() {
        System.out.println("pow");
        int theNumber = 0;
        int power = 0;
        int expResult = 0;
        int result = HaarTransform.pow(theNumber, power);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of joinArrays method, of class HaarTransform.
     */
    @Test
    public void testJoinArrays() {
        System.out.println("joinArrays");
        int[] array1 = null;
        int[] array2 = null;
        int[] expResult = null;
        int[] result = HaarTransform.joinArrays(array1, array2);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of inverseLossy3DArray method, of class HaarTransform.
     */
    @Test
    public void testInverseLossy3DArray() {
        System.out.println("inverseLossy3DArray");
        int[][][] transform = null;
        int height = 0;
        int levelOfLoss = 0;
        byte[][][] expResult = null;
        byte[][][] result = HaarTransform.inverseLossy3DArray(transform, height, levelOfLoss);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of inverseLossyPowerOfTwo method, of class HaarTransform.
     */
    @Test
    public void testInverseLossyPowerOfTwo() {
        System.out.println("inverseLossyPowerOfTwo");
        int[] transform = null;
        int levelOfLoss = 0;
        byte[] expResult = null;
        byte[] result = HaarTransform.inverseLossyPowerOfTwo(transform, levelOfLoss);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of inverseLossyArbitraryLength method, of class HaarTransform.
     */
    @Test
    public void testInverseLossyArbitraryLength() {
        System.out.println("inverseLossyArbitraryLength");
        int[] transform = null;
        int originalSize = 0;
        int levelOfLoss = 0;
        byte[] expResult = null;
        byte[] result = HaarTransform.inverseLossyArbitraryLength(transform, originalSize, levelOfLoss);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
