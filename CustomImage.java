import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.nio.file.Path;

/**
 * Created by Mitch on 7/31/2016.
 */
class CustomImage extends BorderPane {
    private ImageView image;
    private Path path;
    private int fileIndex;
    private boolean selected;
    private boolean owned;
    private PhotoGrid origin;
    private MenuItem add = new MenuItem("Add selection");
    private MenuItem open = new MenuItem("View");
    private MenuItem remove = new MenuItem("Delete selection");
    private MenuItem delete = new MenuItem("Delete selection from disk");
    private MenuItem increaseZoom = new MenuItem("Increase zoom");
    private MenuItem decreaseZoom = new MenuItem("Decrease zoom");
    private SeparatorMenuItem separator = new SeparatorMenuItem();
    private SeparatorMenuItem separator2 = new SeparatorMenuItem();
    private final ContextMenu photoContext = new ContextMenu(open, separator, add, remove, delete, separator2, increaseZoom, decreaseZoom);
    private MenuItem open2 = new MenuItem("View");
    private MenuItem remove2 = new MenuItem("Delete selection");
    private MenuItem delete2 = new MenuItem("Delete selection from disk");
    private MenuItem removeAssigned = new MenuItem("Remove from inspection");
    private final ContextMenu assignedPhotoContext = new ContextMenu(removeAssigned, remove2, delete2);


    CustomImage(Path path, int fileIndex){
        this.path = path;
        this.fileIndex = fileIndex;
        origin = null;
        image = new ImageView(new Image(path.toUri().toString(), 350, 0, true, false));
        image.setPreserveRatio(true);
        this.setMaxHeight(Double.MAX_VALUE);
        selected = false;
        owned = false;
        setCenter(image);
        setDefaultStyle();
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image){
        this.image = image;
        this.image.setPreserveRatio(true);
        this.setCenter(null);
        this.setCenter(this.image);
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }

    public boolean isOwned() {
        return owned;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setOwned(boolean owned) {
        this.owned = owned;
    }

    public MenuItem getAdd() {
        return add;
    }

    public void setAdd(MenuItem add) {
        this.add = add;
    }

    public MenuItem getOpen() {
        return open;
    }

    public void setOpen(MenuItem open) {
        this.open = open;
    }

    public MenuItem getRemove() {
        return remove;
    }

    public void setRemove(MenuItem remove) {
        this.remove = remove;
    }

    public MenuItem getDelete() {
        return delete;
    }

    public void setDelete(MenuItem delete) {
        this.delete = delete;
    }

    public SeparatorMenuItem getSeparator() {
        return separator;
    }

    public void setSeparator(SeparatorMenuItem separator) {
        this.separator = separator;
    }

    public ContextMenu getPhotoContext() {
        return photoContext;
    }

    public MenuItem getOpen2() {
        return open2;
    }

    public void setOpen2(MenuItem open2) {
        this.open2 = open2;
    }

    public MenuItem getRemove2() {
        return remove2;
    }

    public void setRemove2(MenuItem remove2) {
        this.remove2 = remove2;
    }

    public MenuItem getDelete2() {
        return delete2;
    }

    public void setDelete2(MenuItem delete2) {
        this.delete2 = delete2;
    }

    public MenuItem getRemoveAssigned() {
        return removeAssigned;
    }

    public void setRemoveAssigned(MenuItem removeAssigned) {
        this.removeAssigned = removeAssigned;
    }

    public ContextMenu getAssignedPhotoContext() {
        return assignedPhotoContext;
    }

    public MenuItem getIncreaseZoom() {
        return increaseZoom;
    }

    public void setIncreaseZoom(MenuItem increaseZoom) {
        this.increaseZoom = increaseZoom;
    }

    public MenuItem getDecreaseZoom() {
        return decreaseZoom;
    }

    public void setDecreaseZoom(MenuItem decreaseZoom) {
        this.decreaseZoom = decreaseZoom;
    }

    public PhotoGrid getOrigin() {
        return origin;
    }

    public void setOrigin(PhotoGrid origin) {
        this.origin = origin;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if(selected){
            this.setStyle("-fx-border-color: greenyellow;");
            ColorAdjust adjust = new ColorAdjust();
            adjust.setBrightness(-0.5);
            image.setEffect(adjust);
        }else{
            setDefaultStyle();
        }
    }

    private void setDefaultStyle(){
        this.setStyle("-fx-border-color: black; " + "-fx-background-color: transparent; -fx-border-width: 1;");
        image.setEffect(null);
    }
}