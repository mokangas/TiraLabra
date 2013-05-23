
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
     * Extracts one basic color from an integer of form (0,r,g,b), that is:
     * the last byte in the integer describes the blue color,
     * the third byte is green and the second red.
     * @param rgbInt The integer describing the color.
     * @param color The color to be extracted (BLUE = 0, Green = 1, RED = 2)
     * @return The shade of the color.
     */
    public static byte bgrIntegerToColor(int rgbInt, int color){
        return (byte) (rgbInt >> (8 * color)) ;
    }
    
     /**
     * Combines the shades of the three basic colors into an integer.
     * @param data The shades of the three basic colors that will be combined. 
     * data[0] = blue, data[1] = green and data[2] = red.
     * @return An integer in the form (0,r,g,b). That is, the first byte is empty,
     * the second byte describes the color red, the third color green and the fourth
     * color blue.
     */
    public static int colorsToBgrInteger(byte[] data){
        
        int bgrInteger = 0;
        int a;
        
        for (int color = 0; color < NO_OF_COLORS ; color++) {
            if (data[color] > 0) {
                a = (int) data[color];
            } else {
                a = 256 + (int) data[color];
            }
            bgrInteger +=  a << ( 8 * color) ;
        }
        
        return bgrInteger;
    }
    
    
    
}
