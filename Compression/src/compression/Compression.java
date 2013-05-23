
package compression;

import java.io.File;
import java.io.IOException;


public class Compression {

   
    public static void main(String[] args) throws IOException {
     
        File file = new File("c.bmp");
         testOne(file);
        
    }
    
    /**
     * Will be deleted.
     */
    public static void testOne(File file) throws IOException{
        System.out.println(AuxiliaryMethods.bgrIntegerToColor(1, 0));
    }
}
