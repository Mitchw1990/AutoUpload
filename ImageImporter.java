import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitch on 7/31/2016.
 */
class ImageImporter {

    private final Stage stage;
    private Path dirPath;

    public ImageImporter(Stage stage){
        this.stage = stage;
    }

    public ObservableList<Path> importImages() {
        ObservableList<Path> imageList = FXCollections.observableArrayList();
        DirectoryChooser chooser = new DirectoryChooser();
        File dir = chooser.showDialog(stage);
        if (dir != null) {
            imageList.setAll(load(dir.toPath()));
            dirPath = dir.toPath();
        }else{
            return null;
        }
        return imageList;
    }

    private List<Path> load(Path directory) {
        List<Path> files = new ArrayList<>();
        try {
            Files.newDirectoryStream(directory, "*.{jpg,jpeg,png,JPG,JPEG,PNG}").forEach(files::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files ;
    }

    public Path getDirPath() {
        return dirPath;
    }
}
