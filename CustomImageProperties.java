import java.nio.file.Path;

/**
 * Created by mr_robot on 8/31/2016.
 */
public class CustomImageProperties {

    Path path;
    boolean uploaded;

    public CustomImageProperties(){
        path = null;
        uploaded = false;
    }

    public CustomImageProperties(Path path){
        this.path = path;
        uploaded = false;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }
}
