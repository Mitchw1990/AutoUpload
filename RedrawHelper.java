import javafx.application.Platform;

/**
 * Created by Mitch on 8/1/2016.
 */
class RedrawHelper {
    public static void run(Runnable op) {
        if(op == null) throw new IllegalArgumentException("Null operation passed.");

        if(Platform.isFxApplicationThread()) op.run();
        else Platform.runLater(op);
    }
}
