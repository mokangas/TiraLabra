
package compression;

public class SumTreeLevel extends TreeLevel{

    public SumTreeLevel(TreeLevel below){
        super(below);
    }
    
    public SumTreeLevel(byte[][] content){
        super(null);
        createContent(content);
    }

    public SumTreeLevel(byte[][] content, int stepSize){
        super(null);
        createContent(content, stepSize);
    }
    
    
    @Override
    public void createContent() {
        
        long[][] b = below.getContent();
        int size = b.length / 2;
        this.content = new long[size][size];
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                content[i][j] = b[2*i][2*j] + b[2*i+1][2*j] + b[2*i][2*j+1] +b[2*i+1][2*j+1];
            }
        }
    }
    
    public void createContent(byte[][] c){
        
        int size = c.length;
        this.content = new long[size][size];
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                content[i][j] = (long) c[i][j];
            }
        }
    }
    
    public void createContent(byte[][] c, int stepSize){
        int size = c.length / stepSize;
        content = new long[size][size];
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < stepSize; k++) {
                    for (int l = 0; l < stepSize; l++) {
                        content[i][j] += c[i*stepSize+k][j*stepSize+l];
                    }
                }
            }
        }
    }

    
}
