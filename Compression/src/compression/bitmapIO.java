package compression;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BitmapIO {

    private static final int BLUE = 0;
    private static final int GREEN = 1;
    private static final int RED = 2;
    private static final int NO_OF_COLORS = 3;

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
                    bgrData[color][x][y] = AuxiliaryMethods.bgrIntegerToColor(image.getRGB(x, y), color);
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
                image.setRGB(x, y, AuxiliaryMethods.colorsToBgrInteger(colors));
            }
        }
        
        ImageIO.write(image, "bmp", outputFile);
    }

}
