package compression;

import GUI.GraphicalUI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Compression {

    
    /**
     * Launches the gui or chooses the action according to the command line arguments.
     * @param args Check the manual.
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {

        switch (args.length) {
            case 0:
                launchGui();
                break;
            case 1:
                if (args[0].trim().equals("help")) {
                    printHelp();
                } else {
                    System.out.println("Syntax error.");
                }
                break;
            case 2:
                commandLineFromWtfToBmp(args);
                break;
            case 3:
                commandLineFromBmpToWtf(args);
                break;
            case 4:
                commandLineSeries(args);
                break;
            case 5:
                if (args[0].trim().equals("inv")) {
                    commandLineInverseSeries(args);
                } else {
                    System.out.println("Syntax error.");
                }
                break;
            default:
                System.out.println("Syntax error.");
        }
    }

    /**
     * If the program is run from the command line and has three arguments, this
     * method will be called. It transforms a bmp-file to a wtf-file.
     *
     * @param args[0] level of loss of the transform, 0 is lossless.
     * @param args[1] The (path and) name of the bmp file to be tranformed.
     * @param args[2] The (path and) name of the wtf file to which the
     * tranformed data will be saved.
     * @throws IOException
     */
    public static void commandLineFromBmpToWtf(String[] args) throws IOException {
        long time = System.currentTimeMillis();
        int levelOfLoss = Integer.parseInt(args[0]);
        File inputFile = new File(args[1]);
        File outputFile = new File(args[2]);

        System.out.println("Reading data...");
        byte[][][] data = BitmapIO.readFileIntoByteData(inputFile);
        int originalHeight = data[0][0].length; // This hopefully lets the garbage collector destroy the data array.
        System.out.println("Calculating transform...");
        int[][][] transform = HaarTransform.lossyTransfrom(data, levelOfLoss);
        System.out.println("Writing to file " + args[2] + " ...");
        WTFIO.writeMixedData(transform, originalHeight, levelOfLoss, outputFile);

        time = System.currentTimeMillis() - time;
        System.out.println("Ready. Took " + time + " milliseconds.");
    }

    /**
     * Transforms a picture to a series of wtf files with different leveles of
     * loss.
     *
     * @param args[0] The smallest level of loss
     * @param args[1] The biggest level of loss
     * @param args[2] The image to be transformed.
     * @param args[3] The start of the name of the generated wtf files. The
     * names of the files will be args[3]+i+".wtf", where i is the level of the
     * loss.
     * @throws IOException
     */
    public static void commandLineSeries(String[] args) throws IOException {
        long time = System.currentTimeMillis();
        int lowest = Integer.parseInt(args[0]);
        int highest = Integer.parseInt(args[1]);
        String objectName = args[3].trim();

        System.out.println("Reading data...");
        byte[][][] data = BitmapIO.readFileIntoByteData(new File(args[2].trim()));
        int origHeight = data[0][0].length;

        for (int i = lowest; i <= highest; i++) {
            System.out.println("Calculating transform, lol = " + i + "...");
            int[][][] transform = HaarTransform.lossyTransfrom(data, i);
            System.out.println("Writing file " + objectName + i + ".wtf...");
            WTFIO.writeMixedData(transform, origHeight, i, new File(objectName + i + ".wtf"));
        }

        time = System.currentTimeMillis() - time;
        System.out.println("Ready. Took " + time + " milliseconds.");
    }

    /**
     * Transforms a series of wtf files into bmps. This is invese to the mehtod
     * commandLineSeries. The numbers in arguments don't really have to be the
     * real levels of loss, they are used only to identify the files to be
     * converted.
     *
     * @param args[0] = "inverse"
     * @param args[1] The smallest level of loss (actually, the number in the
     * name).
     * @param args[2] The biggest level of loss (actually, the number in the
     * name).
     * @parma args[3] First part of the name of the files to be converted. For
     * files "img1.wtf", "img2.wtf",... this would be "img".
     * @param args[4] First part of the name of the object files. The final
     * names will be args[4]+i+".bmp", where i is the level of loss.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void commandLineInverseSeries(String args[]) throws FileNotFoundException, IOException {
        long time = System.currentTimeMillis();
        int lowest = Integer.parseInt(args[1]);
        int highest = Integer.parseInt(args[2]);
        String protoInput = args[3].trim();
        String protoOutput = args[4].trim();

        for (int i = lowest; i <= highest; i++) {
            System.out.println("Reading file " + protoInput + i + "wtf...");
            WTFIO pic = new WTFIO(new File(protoInput + i + ".wtf"));
            int[][][] transform = pic.readData();
            System.out.println("Retrieving image data...");
            byte[][][] data = HaarTransform.inverseLossyTransform(transform, pic.getOriginalHeight(), pic.getLevelOfLoss());
            System.out.println("Writing file " + protoOutput + i + ".bmp...");
            BitmapIO.writeByteDataIntoBitmap(data, new File(protoOutput + i + ".bmp"));
        }

        time = System.currentTimeMillis() - time;
        System.out.println("Ready. Took " + time + " milliseconds.");
    }

    /**
     * If the program is run from the command line and has three arguments, this
     * mehtod will be called. It transfroms a wtf-file to a bmp-file.
     *
     * @param args[0] The wtf-file to be transformed.
     * @param args[1] The resulting bmp-file.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void commandLineFromWtfToBmp(String[] args) throws FileNotFoundException, IOException {
        long time = System.currentTimeMillis();
        File inputFile = new File(args[0]);
        File outputFile = new File(args[1]);

        System.out.println("Reading file " + args[0] + " ...");
        WTFIO read = new WTFIO(inputFile);
        int[][][] transform = read.readData();
        int originalHeight = read.getOriginalHeight();  // This is to let garbage collector to...
        int levelOfLoss = read.getLevelOfLoss();        // ..destroy the object read.
        System.out.println("Retrieving image data...");
        byte[][][] data = HaarTransform.inverseLossyTransform(transform, originalHeight, levelOfLoss);
        System.out.println("Writing file " + args[1]);
        BitmapIO.writeByteDataIntoBitmap(data, outputFile);

        time = System.currentTimeMillis() - time;
        System.out.println("Ready. Took " + time + " milliseconds.");
    }

    /**
     * Prints the help text.
     */
    public static void printHelp() {
        System.out.println("=============help==========");
        System.out.println("To launch the graphical user interface, type 'java -jar Compression.jar'");
        System.out.println("or double click the jar.file.");
        System.out.println("");
        System.out.println("To use from the command line, type 'java -jar Compression.jar p0 p1,...',");
        System.out.println("where p0, p1 etc. are parameters. Choose them like this");
        System.out.println("-------------");
        System.out.println("To print help:");
        System.out.println("p0 = 'help'");
        System.out.println("");
        System.out.println("To convert image img.bmp to a wtf file transform.wtf with level of loss n:");
        System.out.println("p0 = level of loss");
        System.out.println("p1 = 'img.bmp");
        System.out.println("p2 = 'transform.wtf");
        System.out.println("");
        System.out.println("To convert transform.wtf to a image img.bmp:");
        System.out.println("p0 = 'transform.wtf");
        System.out.println("p1 = 'img.bmp");
        System.out.println("");
        System.out.println("To convert img.bmp to a series of wtf files with levels of loss m, m+1,..., n");
        System.out.println("and names transf[m].wtf, transf[n+1].wtf,...,transf[n].wtf:");
        System.out.println("p0 = m");
        System.out.println("p1 = n");
        System.out.println("p2 = 'img.bmp");
        System.out.println("p3 = 'transf'");
        System.out.println("");
        System.out.println("To convert series of wtf files with names transf[m].wtf, transf[n+1].wtf,...,");
        System.out.println("transf[n].wtf to a series of bmp files with names img[m].bmp, img[m+1].bmp,...");
        System.out.println("img[n].bmp:");
        System.out.println("p0 = 'inv'");
        System.out.println("p1 = m");
        System.out.println("p2 = n");
        System.out.println("p3 = 'transf'");
        System.out.println("p4 = 'img");
        System.out.println("=======End of help=====");
    }

    /**
     * Launches the GUI.
     */
    private static void launchGui() {
        GraphicalUI gui = new GraphicalUI();
    }
}
