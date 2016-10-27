import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Optional;

public class MainApp extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        InspectionView view = new InspectionView(stage);
        view.setVisible(true);
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        stage.setWidth(visualBounds.getWidth() * .9);
        stage.setHeight(visualBounds.getHeight() * .9);
        stage.setX((visualBounds.getWidth() - stage.getWidth())/2);
        stage.setY((visualBounds.getHeight() - stage.getHeight())/2);
        Scene mainScene = new Scene(view);
        InspectionPresenter presenter = new InspectionPresenter(view, stage);
        mainScene.getStylesheets().clear();
        mainScene.getStylesheets().add("style.css");
        view.setScene(mainScene);
        view.attachKeyEvents();
        stage.setScene(mainScene);
        stage.setTitle("Inspection Management");
        stage.setOnCloseRequest(e -> {
            if(getConfirmationExit()) {
                Platform.exit();
            }
        });
        view.exit.setOnAction(e -> {
            if(getConfirmationExit()) {
                Platform.exit();
            }
        });

        stage.show();

        view.toggleFullscreen.selectedProperty().addListener((observable, oldValue, newValue) -> {
            stage.setFullScreen(newValue);
        });

        stage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
           view.toggleFullscreen.setSelected(newValue);
        });
    }

    private boolean getConfirmationExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setContentText("Are you sure you want to exit?");

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }
}