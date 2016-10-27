import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;

/**
 * Created by Mitch on 8/10/2016.
 */
public class PhotoGrid extends GridPane{

    private IntegerProperty columnCount = new SimpleIntegerProperty(3);
    private Tab origin;

    public PhotoGrid(int initialSize){
        super();
        columnCount.setValue(initialSize);
        origin = null;
    }

    public int getColumnCount() {
        return columnCount.get();
    }

    public IntegerProperty columnCountProperty() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount.set(columnCount);
    }

    public Tab getOrigin() {
        return origin;
    }

    public void setOrigin(Tab origin) {
        this.origin = origin;
    }
}
