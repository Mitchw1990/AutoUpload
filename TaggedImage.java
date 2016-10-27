/**
 * Created by Mitch on 8/2/2016.
 */
class TaggedImage {

    private boolean selected;
    private String pathString;
    private int fileIndex;
    private boolean owned;

    public TaggedImage(String pathString, int fileIndex){
        this.pathString = pathString;
        this.fileIndex = fileIndex;
        owned = false;
        selected = false;
    }

    public boolean isOwned() {
        return owned;
    }

    public void setOwned(boolean owned) {
        this.owned = owned;
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPathString() {
        return pathString;
    }

    public void setPathString(String pathString) {
        this.pathString = pathString;
    }

    @Override
    public String toString(){
        return pathString;
    }
}
