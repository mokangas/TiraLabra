package aallokekokeiluja;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;

public class AallokeKokeiluja {

    public static void main(String[] args) throws IOException {

        short[][] data = ekstraktoiData("c.bmp");
        int[][] aallokkeet = summadata(data);
        // kirjoitaTiedostoon(aallokkeet);
        //System.out.println(tarkistaSummat(aallokkeet));;
        
    }

    public static boolean tarkistaSummat(int[][] luvut) {

        for (int i = 0; i < luvut.length; i++) {
            if (!tarkistaRivinSumma(luvut[i])) {
                return false;
            }
        }

        return true;
    }

    private static boolean tarkistaRivinSumma(int[] rivi) {
        int summa = 0;

        for (int j = 0; j < rivi.length / 2; j++) {
            summa += rivi[j];
        }

        int kohta = rivi.length / 2;
        int pituus = kohta / 2;

        while (pituus >= 1) {
            int apusumma = 0;
            for (int i = 0; i < pituus; i++) {
                apusumma += rivi[kohta];
                kohta++;
            }
            if (apusumma != summa) {
                System.out.println(apusumma+"\t"+ summa);
                return false;
            }
            pituus = pituus / 2;
        }
        
        return true;
    }

    public static void kirjoitaTiedostoon(int[][] data) throws IOException {

        FileWriter fw = new FileWriter(new File("summat.txt"));

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                fw.write(" " + data[i][j]);
            }
            fw.write("\n");
        }

        fw.close();
    }

    private static short[][] ekstraktoiData(String filename) throws IOException {
        File f = new File(filename);
        BufferedImage image = ImageIO.read(f);
        short[][] data = new short[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                data[x][y] = (short) (image.getRGB(x, y) & 0xFF);
            }
        }

        return data;
    }

    private static int[][] summadata(short[][] data) {

        int[][] aallokkeet = new int[data.length][data[0].length];
        for (int i = 0; i < aallokkeet.length; i++) {
            summaaRivi(aallokkeet[i], data[i]);
        }

        return aallokkeet;
    }

    private static void summaaRivi(int[] summa, short[] data) {

        // Summataan ensin datat inttiin:
        for (int i = 0; i < data.length / 2; i++) {
            summa[i] = data[2 * i] + data[2 * i + 1];
        }

        // Sitten jatketaan:
        int pituus = data.length / 4;
        int kohta = data.length / 2;

        while (pituus >= 1) {
            for (int i = 0; i < pituus; i++) {
                summa[kohta] = summa[kohta - 2 * pituus] + summa[kohta - 2 * pituus + 1];
                kohta++;
            }
            pituus = pituus / 2;

        }
    }
    
    public static int[][] aallokkeetTheHardWay(int[] data){
        
        int koko = data.length;
        
        int[][] aallokkeet = new int[koko/2][koko/2];
        
        for (int i = 0; i < koko/2; i++) {
            for (int j = 0; j < i; j++) {
                
            }
        }
        
        return aallokkeet;
    }
    
    /**
     * Palauttaa luvun k, jolle
     * k = max{ m : 2^m <= n}
     * @param n
     * @return 
     */
    public static int log2(int n){
        
        int i = 0;
        while ( n >= 1){
            i++;
            n = n/2;
        }
        
        return i-1;
    }
}
