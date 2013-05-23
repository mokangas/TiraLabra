package bmpkokeiluja2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

public class BMPkokeiluja2 {
    
    /**
     * Tämäkin on tehty vain eri asioiden kokeilemiseksi. jotkin metodit ovat
     * tehty aivan leikkimistä varten (esimerkiksi "kuvioi").
     * 
     * kirjoitaKuva kopioi kuvan toiseen tiedostoon
     * kuvaHaviolla kopioi kuvan toiseen tiedostoon, mutta siinä kokeillaan
     *  myös, miltä kuva näyttäisi joidenkin datan vähennystapojen jälkeen. 
     *  Miten dataa tiivistetään riippuu tässä metodissa muokkaaRivia käytetystä
     *  poistaPienet-metodista:
     *  poistaPienet laskee esiintyvät sävyt ja kirjoittaa kuvaan vain ne pikselit,
     *   joita esiintyy enemmän kuin annettu kynnys.
     *  poistaPienet2 maalaa aina koko rivin eniten esiintyvällä sävyllä, paitsi
     *   ne pikselit, jotka ovat jo löytäneet oman sävynsä. 
     *  poistaPienet3 muuttaa pikselin samansävyiseksi kuin sen vasemmalla oleva
     *   paitsi, jos sen oikealla oleva on samansävyinen kuin se nyt on, tai jos
     *   sen vasemmalla olevan pikselin sävyä on muutettu.
     * 
     *  laskeJonojenPituudet laskee, kuinka monta minkäkinpituista samaa sävyä 
     *  olevien pikseleiden jonoa tiedostosta löytyy. Siinä on jokin virhe, joten
     *  arvot eivät ole aivan tarkkoja, mutta niiden suuruusluokan pitäisi olla oikea,
     *  ja se riittää minulle.
     * 
     *  vertaaKasiteltyaKuvaa laskee ensin jonojen pituudet kuvasta, sitten pienentää
     *  sen sisältämän informaation määrää, ja laskee uudesta kuvasta jonot. Tällä 
     *  yritin päästä selville, millaista hyötyä kuvaHaviolla-metodista on.
     * 
     */

    public static void main(String[] args) throws IOException {

        BufferedImage kuva = ImageIO.read(new File("c1.bmp"));
        File kohde = new File("d.bmp");

        // kirjoitaKuva(kuva, kohde);
        // kuvaHaviolla(kuva, kohde,0);
        // poimiPunainen(kuva,2);
        // analysoiHeilahtelu(kuva, new File("kokeilu.txt"));
        // laskeJonojenPituudet(kuva);
        // vertaaKasiteltyaKuvaa(kuva, 3);
        keskiarvoAlueittain(kuva, 20);
    }

    public static void kirjoitaKuva(BufferedImage kirjoitettava, File mihinKirjoitetaan) throws IOException {

        int width = kirjoitettava.getWidth();
        int height = kirjoitettava.getHeight();

        BufferedImage tulos = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                tulos.setRGB(i, j, kirjoitettava.getRGB(kuvioi(i,j, width, height,0), kuvioi(i,j, width, height,1)));
//            }
//        }

        for (int i = 0; i < width; i++) {
            int f = i * i / 10000;
            if (f < height) {
                tulos.setRGB(i, f, 1000);
            }
        }


        ImageIO.write(tulos, "bmp", mihinKirjoitetaan);
    }

    public static int kuvioi(int i, int j, int maxI, int maxJ, int kumpi) {

//        Random r = new Random();
//        int a, aMax;
//        
//        if (kumpi == 0) {
//            a = i;
//            aMax = maxI - 1;
//        } else {
//            a = j;
//            aMax = maxJ - 1;
//        }
//        
//        return Math.min(aMax, a + r.nextInt(100));

        if (kumpi == 1) {
            return j;
        }

        return Math.min(maxI - 1, Math.max(0, (int) (100 * Math.cos(100 * i))));
    }

    public static void kuvaHaviolla(BufferedImage kirjoitettava, File mihinKirjoitetaan, int havio) throws IOException {

        int width = kirjoitettava.getWidth();
        int height = kirjoitettava.getHeight();

        BufferedImage tulos = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        int[] rivi = new int[width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                rivi[j] = kirjoitettava.getRGB(j, i);
            }
            rivi = muokkaaRivia(rivi, havio);
            for (int j = 0; j < width; j++) {
                tulos.setRGB(j, i, rivi[j]);
            }
        }


        ImageIO.write(tulos, "bmp", mihinKirjoitetaan);
    }

    public static int[] muokkaaRivia(int[] rivi, int havio) {

        byte[][] varit = new byte[3][rivi.length];

        for (int i = 0; i < 3; i++) {
            varit[i] = poimiVari(i, rivi);
            varit[i] = poistaPienet3(varit[i], havio);
        }

        for (int i = 0; i < rivi.length; i++) {
            rivi[i] = 0;
            for (int j = 0; j < 3; j++) {
                rivi[i] += (int) ((varit[j][i]) << (8 * j));
            }
        }

        return rivi;
    }

    // Ei kirjoita ollenkaan sävyjä, joita esiintyy pikseleissä
    // vähemmän kuin [kynnys] kpl rivillä.
    public static byte[] poistaPienet(byte[] rivi, int kynnys) {

        int[] kappaletta = new int[256];

        for (int i = 0; i < rivi.length; i++) {
            kappaletta[rivi[i] + 128]++;
        }

        int eniten = etsiSuurimmanIndeksi(kappaletta);
        byte[] palautus = new byte[rivi.length];

        while (eniten >= 0 && kappaletta[eniten] >= kynnys) {
            for (int i = 0; i < rivi.length; i++) {
                if (rivi[i] == eniten - 128) {
                    palautus[i] = rivi[i];
                }
            }
            kappaletta[eniten] = 0;
            eniten = etsiSuurimmanIndeksi(kappaletta);
        }


        return palautus;
    }

    // Maalaa rivin aina eniten esiintyvällä sävyllä muuttamatta kuitenkaan
    // pikseleitä, joiden sävy on jo oikea
    public static byte[] poistaPienet2(byte[] rivi, int kynnys) {

        int[] kappaletta = new int[256];
        boolean[] valmis = new boolean[rivi.length];

        for (int i = 0; i < rivi.length; i++) {
            kappaletta[rivi[i] + 128]++;
            valmis[i] = false;
        }

        int eniten = etsiSuurimmanIndeksi(kappaletta);
        byte[] palautus = new byte[rivi.length];

        while (eniten >= 0 && kappaletta[eniten] >= kynnys) {
            for (int i = 0; i < rivi.length; i++) {
                if (!valmis[i]) {
                    palautus[i] = (byte) (eniten - 128);
                }
                if (rivi[i] == eniten - 128) {
                    valmis[i] = true;
                }
            }
            kappaletta[eniten] = 0;
            eniten = etsiSuurimmanIndeksi(kappaletta);
        }


        return palautus;
    }

    // Jos pikselin molemmat naapurit ovat eri sävyä, ne muutetaan, paitsi 
    // jos edellisen pikselin sävy on muutettu.
    public static byte[] poistaPienet3(byte[] rivi, int kynnys) {

        ArrayList<Integer> muutettavat = new ArrayList<Integer>();
        for (int i = 1; i < rivi.length - 1; i++) {
            if (rivi[i - 1] != rivi[i] && rivi[i] != rivi[i + 1]) {
                muutettavat.add(i);
            }
        }

        for (int i = 0; i < muutettavat.size(); i++) {
            rivi[muutettavat.get(i)] = rivi[muutettavat.get(i) - 1];
            if (i < muutettavat.size() - 1 && muutettavat.get(i + 1) == muutettavat.get(i) + 1) {
                i++;
            }
        }

        return rivi;
    }

    public static int etsiSuurimmanIndeksi(int[] taulukko) {
        int suurin = 0;
        int suurimmanIndeksi = -1;

        for (int i = 0; i < taulukko.length; i++) {
            if (taulukko[i] > suurin) {
                suurin = taulukko[i];
                suurimmanIndeksi = i;
            }
        }

        return suurimmanIndeksi;
    }

    public static byte[] poimiVari(int mikaVari, int[] data) {

        byte[] vari = new byte[data.length];

        for (int i = 0; i < vari.length; i++) {
            vari[i] = (byte) ((data[i] >> (8 * mikaVari)));
        }

        return vari;
    }

    public static void poimiPunainen(BufferedImage kuva, int vari) throws IOException {

        int height = kuva.getHeight();
        int width = kuva.getWidth();

        BufferedImage tulos = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int a = ((kuva.getRGB(i, j) >> (vari * 8)) & 255) << (vari * 8);
                tulos.setRGB(i, j, a);
            }
        }

        ImageIO.write(tulos, "bmp", new File("d.bmp"));
    }

    public static void analysoiHeilahtelu(BufferedImage kuva, File outputFile) throws IOException {

        int laskuri;
        int[] muutoksia = new int[kuva.getHeight()];
        FileWriter teksti = new FileWriter(new File("kokeilu.txt"));

        for (int i = 0; i < kuva.getHeight(); i++) {
            laskuri = 0;
            for (int j = 1; j < kuva.getWidth(); j++) {
                if ((kuva.getRGB(j - 1, i) & 255) != (kuva.getRGB(j, i) & 255)) {
                    laskuri++;
                }
            }
            muutoksia[i] = laskuri;
            laskuri = kuva.getWidth() - laskuri;
            teksti.write(" " + laskuri);
        }

        teksti.close();
    }

    public static void vertaaKasiteltyaKuvaa(BufferedImage kuva, int havio) throws IOException {

        int[] jonojaEkassa = laskeJonojenPituudet(kuva);

        kuvaHaviolla(kuva, new File("d.bmp"), havio);
        BufferedImage haviolla = ImageIO.read(new File("d.bmp"));
        int[] jonojaTokassa = laskeJonojenPituudet(haviolla);

        FileWriter fw = new FileWriter(new File("kokeilu.txt"));
        for (int i = 0; i < jonojaEkassa.length; i++) {
            if (jonojaEkassa[i] != 0 || jonojaTokassa[i] != 0) {
                fw.write("\n" + i + ":\t" + jonojaEkassa[i] + "\t" + jonojaTokassa[i]);
            }
        }
        fw.close();
    }

    public static int[] laskeJonojenPituudet(BufferedImage kuva) throws IOException {

        int height = kuva.getHeight();
        int width = kuva.getWidth();

        int[] tamanPituisiaJonoja = new int[width + 1];

        for (int i = 0; i < height; i++) {
            int j = 1;
            while (j < width) {
                j = laskeJono(kuva, i, j, width, tamanPituisiaJonoja);
                j++;
            }
        }

//        FileWriter fw = new FileWriter(new File("kokeilu.txt"));
//        for (int i = 0; i < tamanPituisiaJonoja.length; i++) {
//            if (tamanPituisiaJonoja[i] != 0) {
//                fw.write("\n" + i + ": " + tamanPituisiaJonoja[i]);
//            }
//        }
//        fw.close();

        int yhteensa = 0;
        for (int i = 0; i < tamanPituisiaJonoja.length; i++) {
            yhteensa += i * tamanPituisiaJonoja[i];
        }

        return tamanPituisiaJonoja;
    }

    private static int laskeJono(BufferedImage kuva, int rivi, int indeksi, int width, int[] tamanPituisiaJonoja) {

        int pituus = 1;

        while (indeksi < width) {
            int ed = kuva.getRGB(indeksi - 1, rivi) & 255;
            int nyk = kuva.getRGB(indeksi, rivi) & 255;
            if (ed != nyk) {
                break;
            }
            indeksi++;
            pituus++;
        }

        tamanPituisiaJonoja[pituus]++;
        return indeksi;
    }

    public static void keskiarvoAlueittain(BufferedImage kuva, int koko) throws IOException {

        int height = kuva.getHeight();
        int width = kuva.getWidth();

        BufferedImage tulos = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        for (int i = 0; i < width - koko - koko; i += koko) {
            for (int j = 0; j < height - koko - koko; j += koko) {
                maalaaAlue(i, j, kuva, tulos, koko);
            }
        }

        ImageIO.write(tulos, "bmp", new File("d.bmp"));
    }

    private static void maalaaAlue(int x, int y, BufferedImage kuva, BufferedImage tulos, int koko) {

        int[] varit = new int[3];
        for (int k = 0; k < 3; k++) {
            for (int i = 0; i < koko; i++) {
                for (int j = 0; j < koko; j++) {
                    varit[j] += savy(kuva.getRGB(x + i, y + j), k);
                }
            }
            varit[k] = varit[k] / (koko * koko);
        }

        
        for (int i = 0; i < koko; i++) {
            for (int j = 0; j < koko; j++) {
                int vari=0;
                for (int k = 0; k < 3; k++) {
                    vari += varit[k] << (8*k);
                }
                tulos.setRGB(x+i,y+j,vari);
            }
        }

    }

    public static byte savy(int luku, int variRGB) {
        return (byte) ((luku >> (8 * variRGB)) & 255);
    }
}