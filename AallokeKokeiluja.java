package aallokekokeiluja;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;

public class AallokeKokeiluja {

    private static final int BLUE = 0;
    private static final int GREEN = 1;
    private static final int RED = 2;
    private static final int NO_OF_COLORS = 3;

    public static void main(String[] args) throws IOException {
        File f = new File("c.bmp");
        byte[][][] data = readFileIntoByteData(f);
        int[] a = muunnaSarakeAallokkeiksi(data[0][0]);
        tulostaBytetJaSummaTaulukko(data[0][0], a);
    }

    public static int[] muunnaSarakeAallokkeiksi(byte[] data) {

        int koko = data.length;
        int[] summat = summaaSarake(data);
        int[] aallokkeet = new int[koko];

        koko = koko / 2;

        for (int i = 0; i < koko; i++) {
            aallokkeet[i] = data[2 * i] - data[2 * i + 1];
        }

        int osoitin = koko;
        koko = koko / 2;
        while (koko >= 1) {
            for (int i = 0; i < koko / 2; i++) {
                aallokkeet[osoitin + i] = summat[osoitin - koko + 2 * i] + summat[osoitin - koko + 2 * i + 1];
            }
            koko = koko / 2;
            osoitin = osoitin + koko;
        }


        return aallokkeet;
    }

    public static byte[][][] readFileIntoByteData(File file) throws IOException {

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

    public static byte bgrIntegerToColor(int rgbInt, int color) {
        rgbInt = rgbInt >> (8 * color); // shift the relevant bits to the end
        rgbInt = rgbInt & 0XFF;          // ignore all the other bits
        rgbInt = rgbInt - 128;           // so that casting into bytes doesn't break distances
        return (byte) rgbInt;
    }

    public static void printByteData(byte[][][] data) {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                for (int k = 0; k < data[0][0].length; k++) {
                    System.out.print(" " + data[i][j][k]);
                }
                System.out.println("");
            }
        }
    }

    /**
     * Laskee summia byte-sarakkeesta: ensiksi kaksi byteä kerrallaan palautus
     * taulukkoon, sitten edellisten perään neljä byteä kerrallaan, niiden
     * perään kahdeksan byteä kerrallaan jne,
     *
     * @param data
     * @return
     */
    public static int[] summaaSarake(byte[] data) {

        int koko = data.length;
        int[] summat = new int[koko];
        int osoitin = 0;

        // ensin kahden summat:
        for (int i = 0; i < koko; i++) {
            summat[i / 2] += data[i];
        }

        // Sitten loput:
        osoitin = koko / 2;
        koko = koko / 2;

        while (koko >= 1) {
            for (int i = 0; i < koko / 2; i++) {
                //System.out.println(""+koko+" "+ (osoitin+i)+"\t");
                summat[osoitin + i] = summat[osoitin - koko + 2 * i] + summat[osoitin - koko + 2 * i + 1];
            }
            koko = koko / 2;
            osoitin += koko;
        }

        return summat;
    }

    public static byte[] palautaAlkuperainen(int[] aallokkeet, int pituus){
        
        byte[] data = new byte[pituus];
        int bitteja = 1;
        int osoitin = 0;
        
        
        return data;
    }
    
    
    /**
     * suurin k s.e. 2^k <= n.
     */
    public static int log2(int n) {
        int log = -1;
        while (n >= 1) {
            log++;
            n = n / 2;
        }
        return log;
    }

    public static void tulostaBytetJaSummaTaulukko(byte[] bytet, int[] summat) throws IOException {

        FileWriter bw = new FileWriter(new File("bytet.txt"));
        FileWriter sw = new FileWriter(new File("summat.txt"));
        for (int i = 0; i < summat.length; i++) {
            bw.write(" " + bytet[i]);
        }

        int koko = summat.length / 2;
        int osoitin = 0;
        while (koko > 1) {
            for (int i = 0; i < koko; i++) {
                // sw.write((osoitin + i) + ":" + summat[osoitin + i] + " "); // numero ja arvo.
                sw.write(" "+ summat[osoitin+i]); // Pelkät arvot
            }
            sw.write("\n======================\n");
            osoitin = osoitin + koko;
            koko = koko / 2;
        }

        bw.close();
        sw.close();
    }
}
