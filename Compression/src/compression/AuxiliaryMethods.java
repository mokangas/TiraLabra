
package compression;


public class AuxiliaryMethods {
    
    
    private static final int BLUE = 0;
    private static final int GREEN = 1;
    private static final int RED = 2;
    private static final int NO_OF_COLORS = 3;
    
    /**
     * This class contains common auxiliary methods that can be used in any other class.
     */
    
    /**
     * Extracts shade of a basic color from the composite integer describing a pixel.
     * The integer must be of form (0,r,g,b), that is: the first byte is meaningless,
     * the second describes the share of red, the third the shade of green and the last
     * one the shade of blue. To preserve the order and distance of the shades 128 is
     * subtracted from the returning value. (Otherwise shades 127 and 128 for example
     * would result 127 and -128 respectively.
     * @param rgbInt The integer describing the color.
     * @param color The color to be extracted (BLUE = 0, Green = 1, RED = 2)
     * @return The shade of the color - 128.
     */
    public static byte bgrIntegerToColor(int rgbInt, int color){
        rgbInt = rgbInt >> (8 * color) ; // shift the relevant bits to the end
        rgbInt = rgbInt & 0XFF;          // ignore all the other bits
        rgbInt = rgbInt - 128;           // so that casting into bytes doesn't break distances
        return (byte) rgbInt;
    }
    
     /**
     * Combines the shades of the three basic colors into a single integer. This 
     * corresponds to the method bgrIntegerToColor, and thus values -128,...,127
     * of data correspond to the shades 0,...,255 of the color.
     * @param data The shades of the three basic colors that will be combined. 
     * data[0] = blue, data[1] = green and data[2] = red.
     * @return An integer in the form (0,r,g,b). That is, the first byte is empty,
     * the second byte describes the color red, the third color green and the fourth
     * color blue.
     */
    public static int colorsToBgrInteger(byte[] data){
        
        int bgrInteger = 0;
        
        for (int c = 0; c < NO_OF_COLORS ; c++) {
            int color = ((int) data[c]) + 128;
            bgrInteger +=  color << ( 8 * c) ;
        }
        
        return bgrInteger;
    }
    
    
    
}
