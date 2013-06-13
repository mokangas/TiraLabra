/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compression;


public class HaarTreeLevel extends TreeLevel {

    
    public HaarTreeLevel(TreeLevel below){
        super(below);
    }
    
 
    
    
    public void createContent(SumTreeLevel sumTreeLevelBelow) {
        
        long[][] b = sumTreeLevelBelow.getContent();
        int size = b.length / 2;
        this.content = new long[size][size];
        
//        if (size == 1) {
//            content[0][0] = b[0][0];
//            return;
//        }
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                content[i][j] = 0;
                for (int k = 0; k < 2; k++) {
                    for (int l = 0; l < 2; l++) {
                        if (k==l) {
                            content[i][j] += b[2*i+k][2*j+l];
                        } else {
                            content[i][j] -= b[2*i+k][2*j+l];
                        }
                    }
                }
            }
        }
        
    }

    @Override
    public void createContent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
