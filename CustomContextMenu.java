import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 * Created by Mitch on 8/10/2016.
 */
class CustomContextMenu extends ContextMenu {

    private final Node owner;

    CustomContextMenu(Node owner, MenuItem ... items){
        super(items);
        this.owner = owner;
    }
}
