package compression;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BitmapIO {

    public static final int BLUE = 0;
    public static final int GREEN = 1;
    public static final int RED = 2;
    public static final int NO_OF_COLORS = 3;

    /**
     * Reads a given bmp-file into data of it's pixels in the form
     * data[c][x][y], where (x,y) tells the location of the pixel in the bitmap and
     * c specifies which basic color's shade is stored in the byte (0= blue,
     * 1 = green and 2 = red). For example data[1][0][0] tells the shade of green of
     * the upper left corner of the image.
     * @param file The file to be read. Must be .bmp.
     * @return the color data.
     * @throws IOException 
     */
    public static byte[][][] readFileIntoByteData(File file) throws IOException{
        
        BufferedImage image = ImageIO.read(file);
        byte[][][] bgrData = new byte[NO_OF_COLORS][image.getWidth()][image.getHeight()];

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                for (int color = 0; color < NO_OF_COLORS; color++) {
                    bgrData[color][x][y] = bgrIntegerToColor(image.getRGB(x, y), color);
                }
            }
        }

        return bgrData;
    }

    /**
     * Writes raw data on the hard disk in .bmp-form. The data must be in the 
     * same form as it is read in the readFileIntoByteData-method.
     * @param data the pixel data of the image.
     * @param outputFile the file into which the image will be written.
     * @throws IOException 
     */
    public static void writeByteDataIntoBitmap(byte[][][] data, File outputFile) throws IOException{
        int height = data[0][0].length;
        int width = data[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                byte[] colors = new byte[data.length];
                for (int i = 0; i < data.length; i++) {
                    colors[i] = data[i][x][y];
                }
                image.setRGB(x, y, colorsToBgrInteger(colors));
            }
        }
        
        ImageIO.write(image, "bmp", outputFile);
    }
    
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
