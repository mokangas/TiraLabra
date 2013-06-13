package compression;

public abstract class TreeLevel {

    long[][] content;
    TreeLevel below;
    TreeLevel above;

    public TreeLevel(TreeLevel below) {
        this.below = below;
        if (below != null) {
            below.setAboveLevel(this);
        }
    }

    public abstract void createContent();

    public long[][] getContent() {
        return content;
    }

    public void setAboveLevel(TreeLevel above) {
        this.above = above;
    }

    TreeLevel getAbove() {
        return above;
    }
    
    
    int getSize() {
        return content.length;
    }
}
