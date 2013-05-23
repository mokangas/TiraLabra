package compression;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class bitmapIO {

    private static final int BLUE = 0;
    private static final int GREEN = 1;
    private static final int RED = 2;
    private static final int NO_OF_COLORS = 3;

    /**
     * Extracts color information into an array where each of the colors blue,
     * green and red are represented separately.
     *
     * @param image The image to be read.
     * @return An array of the colors. Array[i][x][y] tells what is the shade of
     * color i in the coordinates (x,y) of the picture. Colors are (BLUE = 0,
     * GREEN = 1 and RED = 2)-
     */
    private byte[][][] extractBGRdata(BufferedImage image) {

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
     * Combines data of each separate basic color into an image.
     *
     * @param data The color data. Data[i][x][y] tells the shade of color i in
     * the pixel (x,y).
     * @return The image.
     */
    private BufferedImage createImageFromBgrData(byte[][][] data) {

        int h = data[0][0].length;
        int w = data[0].length;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                byte[] colors = new byte[data.length];
                for (int i = 0; i < data.length; i++) {
                    colors[i] = data[i][x][y];
                }
                image.setRGB(x, y, AuxiliaryMethods.colorsToBgrInteger(colors));
            }
        }



        return image;
    }
    
        /**
     * Creates a .bmp-file of the given image onto the hard disk.
     * @param image The image to be saved.
     * @param filename The name of the file into which the image will be saved
     * @throws IOException If the save fails.
     */
    private void writeImageToBmpFile(BufferedImage image, String filename) throws IOException{
        File file = new File(filename);
        ImageIO.write(image, "bmp", file);
    }
}
