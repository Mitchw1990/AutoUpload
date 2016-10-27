import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Created by mrrobot on 8/11/16.
 */
class SettingsPage extends Stage {

    private TextField usernameField = new TextField();
    private TextField passwordField = new TextField();
    private Label usernamelabel = new Label("Username:");
    private Label passwordLabel = new Label("Password:");
    private Label titleLabel = new Label("Uploader Settings");
    private CheckBox systemTrayCheck = new CheckBox("Auto Collapse to System Tray");
    private Button saveButton = new Button("Save");

    private Region spacer = new Region();
    private Region spacer2 = new Region();


    private VBox checkBoxContainer = new VBox(systemTrayCheck);
    private VBox container = new VBox(usernamelabel, usernameField, spacer, passwordLabel, passwordField, spacer2, checkBoxContainer);


    public SettingsPage(){


        UserSettings settings = new UserSettings();

        if(settings.getAutoSystemTray() != null && settings.getAutoSystemTray().equals("true")){
            systemTrayCheck.setSelected(true);
        }else{
            systemTrayCheck.setSelected(false);
        }

        passwordField.setText(settings.getPassword());
        usernameField.setText(settings.getUsername());

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!passwordField.getText().equals(settings.getPassword())){
                saveButton.setVisible(true);
            }
        });

        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!usernameField.getText().equals(settings.getUsername())){
                saveButton.setVisible(true);
            }
        });

        systemTrayCheck.setOnAction(e -> settings.setAutoSystemTray(systemTrayCheck.isSelected()));

        usernameField.textProperty().addListener((observable, oldValue, newValue) -> saveButton.setVisible(true));


        container.maxWidthProperty().bind(this.widthProperty().divide(2.0));
        container.maxHeightProperty().bind(this.heightProperty().divide(3.0));
        titleLabel.maxWidthProperty().bind(this.widthProperty());
        usernameField.maxWidthProperty().bind(container.widthProperty());
        passwordField.maxWidthProperty().bind(container.widthProperty());
        usernamelabel.maxWidthProperty().bind(container.widthProperty());
        passwordLabel.maxWidthProperty().bind(container.widthProperty());
        systemTrayCheck.maxWidthProperty().bind(container.widthProperty());
        usernamelabel.setStyle("-fx-background-color: transparent");
        passwordLabel.setStyle("-fx-background-color: transparent");
        VBox.setVgrow(spacer, Priority.ALWAYS);
        VBox.setVgrow(spacer2, Priority.ALWAYS);

        container.setAlignment(Pos.CENTER);
        container.spacingProperty().bind(this.heightProperty().divide(30));

        saveButton.setOnAction(e -> {
            settings.setUsername(usernameField.getText());
            settings.setPassword(passwordField.getText());
            saveButton.setVisible(false);
        });

        saveButton.setVisible(false);

        BorderPane borderPane = new BorderPane(container);
        borderPane.setTop(titleLabel);
        titleLabel.setStyle("-fx-font-size: 16;" +
                "-fx-alignment: baseline-center");



        GridPane buttonContainer = new GridPane();
        ColumnConstraints c0 = new ColumnConstraints();
        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();
        c0.setPercentWidth(87);
        c1.setPercentWidth(10);
        c2.setPercentWidth(3);
        RowConstraints r0 = new RowConstraints();
        RowConstraints r1 = new RowConstraints();
        RowConstraints r2 = new RowConstraints();
        r0.prefHeightProperty().bind(this.heightProperty().divide(15));
        r1.prefHeightProperty().bind(this.heightProperty().divide(15));
        r2.prefHeightProperty().bind(this.heightProperty().divide(15));
        buttonContainer.getColumnConstraints().addAll(c0,c1,c2);
        buttonContainer.getRowConstraints().addAll(r0, r1, r2);
        buttonContainer.add(saveButton, 1,1);

        borderPane.setBottom(buttonContainer);



        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        this.setWidth(visualBounds.getWidth() * .3);
        this.setHeight(visualBounds.getHeight() * .3);
        this.setX((visualBounds.getWidth() - this.getWidth())/2);
        this.setY((visualBounds.getHeight() - this.getHeight())/2);

        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add("style.css");

        this.setScene(scene);
        this.show();
    }

    private void setCredentials(){

    }



}
