import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
/**
 * Created by Mitch on 8/15/2016.
 */
class UploadDialog extends Stage{
    private ObservableList<Inspection> data;
    private DatePicker datePicker = new DatePicker();
    private Button startButton = new Button("Start");
    private Label title = new Label("Initiate Upload");
    private Label dateLabel = new Label("Date:");
    private Region spacer = new Region();
    private Region spacer2 = new Region();
    private Stage mainStage;

    public UploadDialog(ObservableList<Inspection> data, Stage mainStage){
        this.mainStage = mainStage;
        this.data = data;
        startButton.setOnAction(e ->{
            if(datePicker.getValue() != null){
                LocalDate localDate = datePicker.getValue();
                Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
                Date date = Date.from(instant);
                String dateStr = new SimpleDateFormat("MM/dd/yyyy").format(date);
                UploaderUI form = new UploaderUI(data, mainStage , dateStr);
                Scene scene = new Scene(form);
                mainStage.setScene(scene);
                form.uploadInspections();
                Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
                mainStage.setWidth(visualBounds.getWidth() * .3);
                mainStage.setHeight(visualBounds.getHeight() * .3);
                mainStage.setX((visualBounds.getWidth() - mainStage.getWidth())/2);
                mainStage.setY((visualBounds.getHeight() - mainStage.getHeight())/2);
            }
            this.close();
        });

        VBox container = new VBox(spacer, dateLabel ,datePicker, startButton, spacer2);
        BorderPane borderPane = new BorderPane(container);
        borderPane.setTop(title);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        VBox.setVgrow(spacer2, Priority.ALWAYS);
        container.setSpacing(20.0);
        container.setAlignment(Pos.CENTER);

        title.setStyle("-fx-font-size: 16;" +
                "-fx-alignment: baseline-center");
        title.maxWidthProperty().bind(this.widthProperty());
        dateLabel.setStyle("-fx-background-color: transparent;");
        Scene scene = new Scene(borderPane);
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        this.setWidth(visualBounds.getWidth() * .2);
        this.setHeight(visualBounds.getHeight() * .2);
        this.setX((visualBounds.getWidth() - this.getWidth())/2);
        this.setY((visualBounds.getHeight() - this.getHeight())/2);
        scene.getStylesheets().add("style.css");
        this.setScene(scene);
        this.show();
    }
    public Stage getMainStage() {
        return mainStage;
    }
}
