package bmpkokeiluja;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;

public class BMPkokeiluja {
    
    /**
     * Tässä olen yrittänyt selvittää bmp-tiedoston rakennetta. En vielä tuntenut
     * BufferedImage-luokkaa, joten olen lukenut yksittäisiä bittejä ja selvittänyt
     * tiedostomäärittelyjen (ks. esim. wikipedia) avulla kuvan metatietoja.
     * 
     * Tärkeimmät metodit oliat "lue", joka lukee kuvatiedostosta metadatan sekä
     * "analysoiKuvaRiveittain" ja "analysoiRivienDiversiteetti", joista ensimmäinen
     * kirjoittaa tiedostoon eri pikseleiden punaisen sävyt ja jälkimmäinen laskee,
     * kuinka monta mitäkin sävyä esiintyy.
     * 
     * En itsekään saa enää koodista selvää, mutta sitä ei olekaan tarkoitettu 
     * käytettäväksi jälkeenpäin.
     */
         

    // Kuvan koko 1680 x 1050 (width x height)
    public static void main(String[] args) throws FileNotFoundException, IOException {

        ImageInputStream virta = ImageIO.createImageInputStream(new File("c1.bmp"));

        //lue(virta);

        // kirjoitaTiedostoon(virta, 10000);

        // lueNainMontaBytea(virta, 54, 3 * 1680 * 1050);

        //kopioi(virta);

        // analysoiKuvaRiveittain(virta);
        // analysoiRivienDiversiteetti(virta, 1680, 1050, 53, 0); // c.bmp
        // analysoiRivienDiversiteetti(virta, 3456, 2304, 54, 0);    // c1.bmp
        
    }

    private static void lue(ImageInputStream virta) throws FileNotFoundException, IOException {


        System.out.println("Tyyppi: " + (char) virta.read() + (char) virta.read());

        System.out.println("Koko: " + laskeLukuOudollaTavalla(virta, 4));

        for (int i = 0; i < 4; i++) {
            virta.read();
        }

        System.out.println("Datan alku:" + laskeLukuOudollaTavalla(virta, 4));

        System.out.println("DIB-headerin koko: " + laskeLukuOudollaTavalla(virta, 4));
        // = BITMAPINFOHEADER

        System.out.println("Leveys: " + laskeLukuOudollaTavalla(virta, 4));
        System.out.println("Korkeus: " + laskeLukuOudollaTavalla(virta, 4));
        System.out.println("No of colour planes used: " + laskeLukuOudollaTavalla(virta, 2));
        System.out.println("No of bits/pixel: " + laskeLukuOudollaTavalla(virta, 2));
        // The 24-bit pixel (24bpp) format supports 16,777,216 distinct colors and stores 1 pixel 
        // value per 3 bytes. Each pixel value defines the red, green and blue samples of the pixel 
        // (8.8.8.0.0 in RGBAX notation). Specifically in the order (blue, green and red, 8-bits per each sample).
        System.out.println("Compression used: " + laskeLukuOudollaTavalla(virta, 4)); // = NONE
        System.out.println("Raw bit data size: " + laskeLukuOudollaTavalla(virta, 4));
        System.out.println("Horizontal resolution: " + laskeLukuOudollaTavalla(virta, 4));
        System.out.println("Vertical resolution: " + laskeLukuOudollaTavalla(virta, 4));
        System.out.println("No of colours: " + laskeLukuOudollaTavalla(virta, 4));


    }

    private static void lue2(ImageInputStream virta) throws IOException {
        Scanner nappaimisto = new Scanner(System.in);
        while (true) {
            System.out.print(virta.read());
            if (nappaimisto.nextLine().equals("lopeta")) {
                break;
            }
        }
    }

    private static void kirjoitaTiedostoon(ImageInputStream virta, int paljonko) throws IOException {

//        virta.reset();

        FileWriter kirjoitin = new FileWriter(new File("kokeilu.txt"));
        int[] bitit = new int[paljonko];
        int suurin = 0;

        for (int i = 0; i < paljonko; i++) {
            bitit[i] = virta.readBit();
            if (bitit[i] > suurin) {
                suurin = bitit[i];
            }
        }

//        int suurinPituus = 0;
//        while (suurin > 0) {
//            suurinPituus++;
//            suurin /= 10;
//        }
//
//        String k = " ";
//        for (int j = 0; j < suurinPituus; j++) {
//            k = k + " ";
//        }

        kirjoitin.append("0: ");
        for (int i = 0; i < paljonko; i++) {
            kirjoitin.append("" + bitit[i]);
            if ((i + 1) % 8 == 0) {
                kirjoitin.append("\n" + ((i + 1) / 8) + ": ");
            }
        }

        kirjoitin.flush();
        kirjoitin.close();
    }

    public static int laskeLukuOudollaTavalla(ImageInputStream virta, int tavuja) throws IOException {
        int luku = 0;
        for (int i = 0; i < tavuja; i++) {
            int k = tavuLittleEndian(virta);
            luku += k << (i * 8);
        }
        return luku;
    }

    public static int tavuLittleEndian(ImageInputStream virta) throws IOException {
        int luku = 0;
        for (int i = 7; i >= 0; i--) {
            if (virta.readBit() == 1) {
                luku += 1 << i;
            }
        }
        return luku;
    }

    private static void tulosta(ImageInputStream virta, int paikka) throws IOException {
        virta.seek(paikka - 200);
        for (int i = 1; i <= 10000; i++) {
            System.out.print(virta.read() + " ");
            if (i % 100 == 0) {
                System.out.println("");
            }
        }
    }

    public static void lueNainMontaBytea(ImageInputStream virta, int offset, int byteja) {
        int luettu = 0;
        try {
            virta.seek(offset);
            for (int i = 0; i < byteja; i++) {
                if (virta.read() < 0) {
                    System.out.println("Virta loppui " + luettu);
                    break;
                }
                luettu++;
            }
            System.out.println("Luettiin " + luettu + " byteä.");
        } catch (IOException ex) {
            System.out.println("Virhe " + luettu);
            Logger.getLogger(BMPkokeiluja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void kopioi(ImageInputStream virta) throws FileNotFoundException, IOException {

        BufferedImage tulos = new BufferedImage(1680, 1050, BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < 54; i++) {
            virta.readByte();
        }

        int laskuri = 0;
        for (int i = 0; i < 1050; i++) {
            for (int j = 0; j < 1680; j++) {
                int luku = 0;
                for (int k = 0; k < 3; k++) {
                    luku += laskeLukuOudollaTavalla(virta, 1) << (8 * k);
                }
                tulos.setRGB(j, 1049 - i, luku);
            }

        }

        File outputfile = new File("d.bmp");
        ImageIO.write(tulos, "bmp", outputfile);

    }

    public static void analysoiKuvaRiveittain(ImageInputStream virta) throws IOException {

        int[][] variaKappaletta = new int[256][30];
        virta.seek(54);

        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 1680; j++) {
                for (int k = 0; k < 3; k++) {
                    int vari = laskeLukuOudollaTavalla(virta, 1);
                    if (k % 3 == 0) {
                        variaKappaletta[vari][i]++;
                    }
                }
            }
        }

        File f = new File("kokeilu.txt");
        FileWriter fw = new FileWriter(f);
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < variaKappaletta.length; j++) {
                fw.write(" "+variaKappaletta[j][i]);
                if (variaKappaletta[j][i] < 100) {
                    fw.write(" ");
                    if (variaKappaletta[j][i] < 10) {
                        fw.write(" ");
                    }
                }
            }
            fw.write("\n");
        }
        fw.flush();
        fw.close();
    }
    
    public static void analysoiRivienDiversiteetti(ImageInputStream virta, int width, int height, int offset, int rgb) throws IOException{
        
        int[][] variaKappaletta = new int[width][256];
        virta.seek(offset);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                for (int k = 0; k < 3; k++) {
                    int vari = laskeLukuOudollaTavalla(virta, 1);
                    if (k % 3 == rgb) {
                        variaKappaletta[i][vari]++;
                    }
                }
            }
        }

        File f = new File("kokeilu.txt");
        FileWriter fw = new FileWriter(f);
        
        for (int i = 0; i < height; i++) {
            while (kirjoitaSuurinLukuTiedostoon(fw, variaKappaletta[i])){
            }
        }
        
        fw.flush();
        fw.close();
    }
    
    public static boolean kirjoitaSuurinLukuTiedostoon(FileWriter fw, int[] taulukko) throws IOException{
        int suurinToistaiseksi = 0;
        int suurimmanPaikka = -1;
        
        for (int i = 0; i < taulukko.length; i++) {
            if (taulukko[i] > suurinToistaiseksi) {
                suurimmanPaikka = i;
                suurinToistaiseksi = taulukko[i];
            }
        }
        
        if (suurinToistaiseksi == 0) {
            fw.write("\n");
            return false;
        } 
        
        fw.write(" "+suurinToistaiseksi);
        taulukko[suurimmanPaikka] = 0;
        return true;
    }
}