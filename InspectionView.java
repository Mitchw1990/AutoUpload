import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

class InspectionView extends GridPane {
        private int columnsPerPhotoGrid = 3;
        private final int TOTAL_GRID_ROWS = 40;
        private Inspection currentInspection = null;
        private volatile ObservableList<CustomImage> selectedPhotos = FXCollections.observableArrayList();
        private volatile ObservableList<CustomImage> assignedSelectedPhotos = FXCollections.observableArrayList();
        private volatile ObservableList<PhotoGrid> photoGridList = FXCollections.observableArrayList();
        volatile ObservableList<Inspection> inspectionList = FXCollections.observableArrayList();
        private ScrollPane selectedPhotosScroller = new ScrollPane();
        private HBox assignedPhotosContainer = new HBox();
        private Tab dataTab  = new Tab("Data View");
        private TabPane tabPane = new TabPane(dataTab);
        private TableView<Inspection> dataTable = new TableView<>();
        private TextField brokerNameField = new TextField();
        private TextField brokerNumberField = new TextField();
        private Label occupancyLabel = new Label(" Occupancy:");
        private Label propertyTypeLabel = new Label(" Property:");
        private Label constructionTypeLabel = new Label(" Construction:");
        private Label propertyConditionLabel = new Label(" Condition:");
        private Label forSaleLabel = new Label(" For Sale:");
        private Label determinedByLabel = new Label(" Determined by:");
        private Label brokerNameLabel = new Label(" Broker Name:");
        private Label brokerNumberLabel = new Label(" Broker Number:");
        private Label roofTypeLabel = new Label(" Roof:");
        private Label landscapeTypeLabel = new Label(" Yard Landscape:");
        private Label landscapeConditionLabel = new Label(" Yard Condition:");
        private Label grassHeightLabel = new Label(" Grass Height:");
        private Label garageTypeLabel = new Label(" Garage:");
        private Label spokeWithLabel = new Label(" Spoke With:");
        private Label wellsFargoFieldsLabel = new Label("Wells Fargo Fields");
        private Label standardFieldsLabel = new Label("Standard Fields");
        private Label globalFieldsLabel = new Label("Globals Fields");
        private Label occupancyIndicatorsLabel = new Label("Occupancy Indicators");
        private ComboBox<String> occupancyField = new ComboBox<>();
        ComboBox<String> forSaleField = new ComboBox<>();
        private ComboBox<String> determinedByField = new ComboBox<>();
        private ComboBox<String> spokeWithField = new ComboBox<>();
        private ComboBox<String> propertyTypeField = new ComboBox<>();
        private ComboBox<String> constructionTypeField = new ComboBox<>();
        private ComboBox<String> propertyConditionField = new ComboBox<>();
        private ComboBox<String> roofTypeField = new ComboBox<>();
        private ComboBox<String> landscapeTypeField = new ComboBox<>();
        private ComboBox<String> landscapeConditionField = new ComboBox<>();
        private ComboBox<String> grassHeightField = new ComboBox<>();
        private ComboBox<String> garageTypeField = new ComboBox<>();
        private ComboBox<String> noAccessReasonCombo = new ComboBox<>();
        private ContextMenu currentContext = null;
        private Label totalInspectionsLabel = new Label();
        private Label totalCompleteLabel = new Label();
        private Label inspectionTypeLabel = new Label();
        private Label addressLabel = new Label();
        private CheckBox noAccessBox = new CheckBox("No Access");
        private CheckBox autoCommentBox = new CheckBox("Auto-Generate Comment");
        private TextArea customCommentField = new TextArea();
        private Label customCommentLabel = new Label("Custom Comment:");
        private HBox statusBar = new HBox();
        private Menu mainMenu = new Menu("File");
        private Menu importMenu = new Menu("Import...");
        public MenuItem importPhotos = new MenuItem("Import photos");
        MenuItem importSpreadsheet = new MenuItem("Import spreadsheet");
        private MenuItem options = new MenuItem("options");
        private SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem exit = new MenuItem("Exit");
        private Menu uploadMenu = new Menu("Uploader");
        MenuItem startUpload = new MenuItem("Launch Uploader");
        MenuItem settings = new MenuItem("Settings");
        private Menu viewMenu = new Menu("View");
        private Menu photoViewerMenu = new Menu("Photo Viewer");
        CheckMenuItem toggleFullscreen = new CheckMenuItem("Toggle Fullscreen");
        private Menu helpMenu = new Menu("Help");
        private MenuItem viewShortcuts = new MenuItem("View Shortcuts");
        private MenuBar menuBar = new MenuBar(mainMenu, uploadMenu, viewMenu, helpMenu);
        private Stage stage;
        private volatile IntegerProperty totalCompete = new SimpleIntegerProperty(0);
        private HBox status = new HBox(totalCompleteLabel, totalInspectionsLabel);
        private List<String> items = Arrays.asList("Lawn Cut", "Car/Boat", "Decorations", "People", "Furniture in House");
        private ListView<String> occupancyIndicators =   new ListView<>(FXCollections.observableArrayList(items));
        private boolean occupancyIndicatorsActive = false;
        private Scene scene = null;
        private Tab photoTab = new Tab();
        private boolean goProMode = true;
        private Tab trashTab = new Tab("Garbage");
        private ObservableList<Path> trashList = FXCollections.observableArrayList();

        public int getTotalCompete() {
            return totalCompete.get();
        }

        public IntegerProperty totalCompeteProperty() {
            return totalCompete;
        }

        private void setTotalCompete(int totalCompete) {
            this.totalCompete.set(totalCompete);
        }

        public void setScene(Scene scene){
            this.scene = scene;
        }

        public void attachKeyEvents(){
            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                KeyCode code = event.getCode();
                if(dataTable.getItems() != null) {
                    if (code == KeyCode.LEFT) {
                        dataTable.getSelectionModel().selectPrevious();
                        event.consume();
                    } else if (code == KeyCode.RIGHT) {
                        dataTable.getSelectionModel().selectNext();
                        event.consume();
                    }
                }
            });
        }

        public InspectionView(Stage stage){
            this.stage = stage;
            layoutForm();
        }

        private void updateNumberComplete(){
            Platform.runLater(() -> {
                int total = 0;
                for (Inspection i : inspectionList) {
                    if (i.isComplete()) {
                        total++;
                    }
                }
                setTotalCompete(total);
            });
        }

        private void layoutForm(){
            noAccessReasonCombo.getItems().addAll("Gated Community", "Access Code Required", "Guard", "No Trespassing");
            customCommentField.setWrapText(true);
            occupancyIndicators.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            status.setStyle("-fx-alignment: baseline-center; -fx-background-color: #424241;");
            totalInspectionsLabel.setStyle("-fx-font-size: 20; -fx-background-color: #424241; -fx-border-width:0 ");
            totalCompleteLabel.setStyle("-fx-font-size: 20; -fx-background-color: #424241; -fx-border-width:0");
            inspectionTypeLabel.setStyle("-fx-font-size: 20; -fx-background-color: #424241; -fx-border-width:0");
            addressLabel.setStyle("-fx-font-size: 20; -fx-background-color: #424241; -fx-border-width:0");
            totalCompleteLabel.textProperty().bind(totalCompete.asString().concat(" of "));
            statusBar.setStyle("-fx-background-color: #424241;");
            Region spacer = new Region();
            Region spacer2 = new Region();
            Region spacer3 = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox.setHgrow(spacer3, Priority.ALWAYS);
            spacer2.prefWidthProperty().bind(this.widthProperty().multiply(.05));
            statusBar.getChildren().addAll(status, spacer, inspectionTypeLabel, spacer2, addressLabel, spacer3);
            status.setVisible(false);

            populateCombos();
            setUpDataTable();
            insertGridRows(TOTAL_GRID_ROWS);

            this.setHgap(5);
            this.setVgap(5);

            int col0 = 1;
            int col1 = 8;
            int col2 = 10;
            int col3 = 1;
            int col4 = 8;
            int col5 = 10;
            int col6 = 1;
            int col8 = 1;
            int col7 = (100
                    - col0 - col1
                    - col2 - col3
                    - col4 - col5
                    - col6 - col8);

            insertGridColumns(col0,
                    col1, col2, col3,
                    col4, col5, col6,
                    col7, col8);

            globalFieldsLabel.setId("center-label-style");
            wellsFargoFieldsLabel.setId("center-label-style");
            standardFieldsLabel.setId("center-label-style");
            occupancyIndicatorsLabel.setId("center-label-style");
            customCommentLabel.setId("center-label-style");


            dataTab.setContent(dataTable);
            selectedPhotosScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            selectedPhotosScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            assignedPhotosContainer.setAlignment(Pos.CENTER);
            selectedPhotosScroller.setContent(assignedPhotosContainer);
            assignedPhotosContainer.setFillHeight(true);


            mainMenu.getItems().addAll(importMenu, options, separator, exit);
            importMenu.getItems().addAll(importPhotos, importSpreadsheet);
            uploadMenu.getItems().addAll(startUpload, settings);
            viewMenu.getItems().addAll(photoViewerMenu, toggleFullscreen);
            helpMenu.getItems().addAll(viewShortcuts);

            menuBar.setUseSystemMenuBar(true);
            BorderPane menuContainer = new BorderPane();
            menuContainer.setTop(menuBar);
            menuBar.prefHeightProperty().bind(menuContainer.heightProperty());
            menuBar.prefWidthProperty().bind(menuContainer.widthProperty());

            Button fillButton = new Button("Fill");
            fillButton.setOnAction(e -> fillFormDefault());
            dataTab.setClosable(false);
            importPhotos.setDisable(true);
            trashTab.setClosable(false);

            tabPane.getTabs().add(photoTab);
            photoTab.setClosable(false);
            this.add(statusBar, 0, 40, 9, 1);
            this.add(menuContainer, 0, 0, 9 , 1);
            this.add(selectedPhotosScroller, 1, 28, 5, 11);
            this.add(tabPane, 7, 2, 1, 37);
            this.add(occupancyIndicatorsLabel, 1, 20, 2, 2);
            this.add(occupancyIndicators, 1, 22, 2 , 5);

            int colNum = 1;

            this.add(globalFieldsLabel, colNum, 2, 2, 2);
            this.add(occupancyLabel, colNum, 4, 1, 2);
            this.add(propertyTypeLabel, colNum, 6, 1, 2);
            this.add(constructionTypeLabel, colNum, 8, 1, 2);
            this.add(propertyConditionLabel, colNum, 10, 1, 2);
            this.add(forSaleLabel, colNum, 12, 1, 2);
            this.add(determinedByLabel, colNum, 14, 1, 2);
            this.add(brokerNameLabel, colNum, 16, 1, 2);
            this.add(brokerNumberLabel, colNum, 18, 1, 2);

            colNum++;

            this.add(occupancyField, colNum, 4, 1, 2);
            this.add(propertyTypeField, colNum, 6, 1, 2);
            this.add(constructionTypeField, colNum, 8, 1, 2);
            this.add(propertyConditionField, colNum, 10, 1, 2);
            this.add(forSaleField, colNum, 12, 1, 2);
            this.add(determinedByField, colNum, 14, 1, 2);
            this.add(brokerNameField, colNum, 16, 1, 2);
            this.add(brokerNumberField, colNum, 18, 1, 2);

            colNum = 4;

            this.add(wellsFargoFieldsLabel, colNum, 2, 2, 2);
            this.add(roofTypeLabel, colNum, 4, 1, 2);
            this.add(landscapeTypeLabel, colNum, 6, 1, 2);
            this.add(landscapeConditionLabel, colNum, 8, 1, 2);
            this.add(grassHeightLabel, colNum, 10, 1, 2);
            this.add(standardFieldsLabel, colNum, 12, 2, 2);
            this.add(garageTypeLabel, colNum, 14, 1, 2);
            this.add(spokeWithLabel, colNum, 16, 1, 2);
            colNum++;

            this.add(roofTypeField, colNum, 4, 1, 2);
            this.add(landscapeTypeField, colNum, 6, 1, 2);
            this.add(landscapeConditionField, colNum, 8, 1, 2);
            this.add(grassHeightField, colNum, 10, 1, 2);

            this.add(garageTypeField, colNum, 14, 1, 2);
            this.add(spokeWithField, colNum, 16, 1, 2);

            this.add(noAccessBox, --colNum, 18, 1, 2);
            this.add(noAccessReasonCombo, ++colNum, 18, 1, 2);
            this.add(autoCommentBox, --colNum, 20, 2, 2);

            this.add(fillButton, ++colNum, 20, 2, 2);

            this.add(customCommentLabel, --colNum, 22, 2, 2);
            this.add(customCommentField, colNum, 24, 2, 3);


            this.getChildren()
                    .stream()
                    .filter(n -> n instanceof ComboBox ||
                            n instanceof  TextField ||
                            n instanceof Label ||
                            n instanceof Button
                    )
                    .forEach(n -> {
                                Control c = (Control) n;
                                c.setMaxWidth(Double.MAX_VALUE);
                                c.setMaxHeight(Double.MAX_VALUE);
                            }
                    );

            this.getChildren()
                    .stream()
                    .filter(n -> n instanceof ComboBox)
                    .forEach(n -> {
                                ComboBox combo = (ComboBox) n;
                                combo.valueProperty().addListener((observable, oldValue, newValue) -> {
                                    save();
                                    updateNumberComplete();
                                });
                            }
                    );
            this.getChildren()
                    .stream()
                    .filter(n -> n instanceof TextField)
                    .forEach(n -> {
                                TextField field = (TextField) n;
                                field.textProperty().addListener((observable, oldValue, newValue) -> {
                                    save();
                                    updateNumberComplete();
                                });
                            }
                    );

            occupancyIndicators.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if(occupancyIndicatorsActive) {
                    currentInspection.getOccupancyIndicatorList().clear();
                    currentInspection.getOccupancyIndicatorList().addAll(occupancyIndicators.getSelectionModel().getSelectedItems());
                    save();
                }
            });

            determinedByField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (newValue.toLowerCase().contains("visual")) {
                        occupancyIndicators.setDisable(false);
                    }else{
                    occupancyIndicators.setDisable(true);
                    occupancyIndicators.getSelectionModel().clearSelection();
                    }
                    if (!currentInspection.isWellsFargo()) {
                        if (newValue.toLowerCase().contains("direct")) {
                            spokeWithField.setDisable(false);
                        }else{
                            spokeWithField.setDisable(true);
                            spokeWithField.getSelectionModel().clearSelection();
                        }
                    }
                }
            });
            forSaleField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (newValue.toLowerCase().contains("broker")) {
                        brokerNameField.setDisable(false);
                        brokerNumberField.setDisable(false);
                    } else {
                        brokerNameField.setDisable(true);
                        brokerNameField.clear();
                        brokerNumberField.setDisable(true);
                        brokerNumberField.clear();
                    }
                }
            });
            customCommentField.disableProperty().bind(autoCommentBox.selectedProperty());
            noAccessReasonCombo.disableProperty().bind(noAccessBox.selectedProperty().not());
            noAccessReasonCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                save();
            });

            customCommentField.textProperty().addListener((observable, oldValue, newValue) -> {
                currentInspection.setCustomComment(customCommentField.getText());
                save();
            });
            autoCommentBox.setSelected(true);
            autoCommentBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                currentInspection.setAutoComment(newValue);
                if(newValue){
                    customCommentField.setText(null);
                }
                save();
            });

            noAccessBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue){
                    disableAndClearAll();
                }else{
                    setAllFieldsAvailability();
                }
                currentInspection.setNoAccess(newValue);
                save();
            });

            assignedSelectedPhotos.addListener(new ListChangeListener<CustomImage>() {
                @Override
                public void onChanged(Change<? extends CustomImage> c) {
                    if(assignedSelectedPhotos.isEmpty()){
                        for(Node n : assignedPhotosContainer.getChildren()){
                            CustomImage photo = (CustomImage) n;
                            photo.setSelected(false);
                        }
                    }
                }
            });

            selectedPhotos.addListener(new ListChangeListener<CustomImage>() {
                @Override
                public void onChanged(Change<? extends CustomImage> c) {
                    if(selectedPhotos.isEmpty()){
                       for(PhotoGrid grid : photoGridList){
                           for(Node n : grid.getChildren()){
                               CustomImage photo = (CustomImage) n;
                               photo.setSelected(false);
                           }
                       }
                    }
                }
            });

            disableAndClearAll();
            autoCommentBox.setDisable(true);
            noAccessBox.setDisable(true);
        }

        private void insertGridRows(int rows){
            double percentHeight = 100.0/rows;
            for(int i = 0; i <= rows; i++){
                RowConstraints row = new RowConstraints();
                row.setPercentHeight(percentHeight);
                this.getRowConstraints().add(row);
            }
        }

        private void insertGridColumns(int ... percentages){
            for(int p : percentages){
                ColumnConstraints col = new ColumnConstraints();
                col.setPercentWidth(p);
                this.getColumnConstraints().add(col);
            }
        }

        private void disableAndClearAll(){
            this.getChildren()
                    .stream()
                    .filter(n -> n instanceof Control)
                    .forEach(n -> {
                                Control control = (Control) n;
                                if(control instanceof ComboBox){
                                    ComboBox combo = (ComboBox) control;
                                    if(combo != noAccessReasonCombo) {
                                        combo.getSelectionModel().clearSelection();
                                        combo.setDisable(true);
                                    }
                                }else if(control instanceof TextField){
                                    TextField field = (TextField) control;
                                        field.clear();
                                        field.setDisable(true);
                                }
                            }
                    );
                occupancyIndicators.getSelectionModel().clearSelection();
                occupancyIndicators.setDisable(true);
            }

        public void drawPhotoGridTrash(){
            System.gc();

            if(!tabPane.getTabs().contains(trashTab)){
                tabPane.getTabs().add(trashTab);
            }

            Task<ScrollPane> task = new Task<ScrollPane>(){
                    @Override
                    protected ScrollPane call() throws Exception {
                        Iterator<Path> iterator = trashList.iterator();
                        PhotoGrid photoGrid = new PhotoGrid(columnsPerPhotoGrid);
                        photoGridList.add(photoGrid);
                        photoGrid.columnCountProperty().addListener(e -> redrawPhotoGrid(photoGrid));
                        photoGrid.prefWidthProperty().bind(tabPane.widthProperty());
                        photoGrid.setStyle("-fx-background-color: black;");
                        ScrollPane scroll = new ScrollPane();
                        scroll.prefWidthProperty().bind(tabPane.widthProperty());
                        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
                        scroll.setFitToWidth(true);
                        scroll.setContent(photoGrid);
                        int rows = (int) (Math.ceil((double) trashList.size() / photoGrid.getColumnCount()));
                        int counter = -1;
                        for (int r = 0; r < rows; r++) {
                            for (int c = 0; c < photoGrid.getColumnCount(); c++) {
                                if (iterator.hasNext()) {
                                    counter++;
                                    CustomImage photo = new CustomImage(iterator.next(), counter);
                                    photoGrid.add(photo, c, r);
                                    initPhotoMain(photo, photoGrid);
                                    attachClickEvent(photo, false);
                                    updateProgress((double) counter + 1, (double) trashList.size());
                                }
                            }
                        }
                        return scroll;
                    }
                };

            ProgressIndicator indicator = new ProgressIndicator(0);
            indicator.progressProperty().bind(task.progressProperty());
            HBox indicatorContainer = new HBox(indicator);
            indicatorContainer.setAlignment(Pos.CENTER);
            indicator.prefHeightProperty().bind(tabPane.widthProperty().divide(3.0));
            indicator.prefWidthProperty().bind(tabPane.widthProperty().divide(3.0));
            indicator.maxWidthProperty().bind(tabPane.widthProperty().divide(3.0));
            indicator.maxHeightProperty().bind(tabPane.widthProperty().divide(3.0));

            task.setOnSucceeded(e -> {
                trashTab.setContent(task.getValue());
                PhotoGrid grid = (PhotoGrid) task.getValue().getContent();
                grid.setOrigin(trashTab);
                photoTab.setText(currentInspection.getAddress());
            });

            trashTab.setContent(indicatorContainer);
            new Thread(task).start();
        }

    public void drawPhotoGrid(boolean newImport){
        System.gc();
        List<Path> files = new ArrayList<>();
        String dirName = "";
        File dir = null;
        ObservableList<Path> list = null;

        if(newImport){
            files = new ImageImporter(stage).importImages();
            if(files != null && files.size() > 0) {
                dirName = files.get(0).getParent().getFileName().toString();
                dir = new File(files.get(0).toUri());
            }
            for(Inspection inspection : inspectionList) {
                for(CustomImageProperties ip : inspection.getImageList()){
                    if(files.contains(ip.getPath())){
                        files.remove(ip.getPath());
                    }
                }
            }

            for(PhotoGrid grid : photoGridList) {
                for(Node n : grid.getChildren()) {
                    CustomImage photo = (CustomImage) n;
                    if(files.contains(photo.getPath())) {
                        files.remove(photo.getPath());
                    }
                }
            }
            list = FXCollections.observableList(files);
        }else {
            dirName = "GoPro" + File.separatorChar + currentInspection.getPropertyId() + File.separatorChar;
            dir = new File(dirName);
            if (dir.exists()) {
                try {
                    Files.newDirectoryStream(dir.toPath(), "*.{jpg,jpeg,png,JPG,JPEG,PNG}").forEach(files::add);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                list = FXCollections.observableList(files);
                for (Inspection inspection : inspectionList) {
                    for (CustomImageProperties ip : inspection.getImageList()) {
                        if (list.contains(ip.getPath())) {
                            list.remove(ip.getPath());
                        }
                    }
                }
            }
        }
        ObservableList<Path> finalList = list;
        Task<ScrollPane> task = new Task<ScrollPane>(){
            @Override
            protected ScrollPane call() throws Exception {
                Iterator<Path> iterator = finalList.iterator();
                PhotoGrid photoGrid = new PhotoGrid(columnsPerPhotoGrid);
                if(newImport){
                    photoGridList.add(photoGrid);
                }
                photoGrid.columnCountProperty().addListener(e -> redrawPhotoGrid(photoGrid));
                photoGrid.prefWidthProperty().bind(tabPane.widthProperty());
                photoGrid.setStyle("-fx-background-color: black;");
                ScrollPane scroll = new ScrollPane();
                scroll.prefWidthProperty().bind(tabPane.widthProperty());
                scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
                scroll.setFitToWidth(true);
                scroll.setContent(photoGrid);
                int rows = (int) (Math.ceil((double) finalList.size() / photoGrid.getColumnCount()));
                int counter = -1;
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < photoGrid.getColumnCount(); c++) {
                        if (iterator.hasNext()) {
                            counter++;
                            CustomImage photo = new CustomImage(iterator.next(), counter);
                            photoGrid.add(photo, c, r);
                            initPhotoMain(photo, photoGrid);
                            attachClickEvent(photo, false);
                            updateProgress((double) counter + 1, (double) finalList.size());
                        }
                    }
                }
                return scroll;
            }
        };

        ProgressIndicator indicator = new ProgressIndicator(0);
        indicator.progressProperty().bind(task.progressProperty());
        HBox indicatorContainer = new HBox(indicator);
        indicatorContainer.setAlignment(Pos.CENTER);
        indicator.prefHeightProperty().bind(tabPane.widthProperty().divide(3.0));
        indicator.prefWidthProperty().bind(tabPane.widthProperty().divide(3.0));
        indicator.maxWidthProperty().bind(tabPane.widthProperty().divide(3.0));
        indicator.maxHeightProperty().bind(tabPane.widthProperty().divide(3.0));

        if(newImport){
            String finalDirName = dirName;
            Tab newTab = new Tab(finalDirName);
            task.setOnSucceeded(e -> {
                newTab.setContent(task.getValue());
                PhotoGrid grid = (PhotoGrid) task.getValue().getContent();
                grid.setOrigin(newTab);
                newTab.setOnClosed(event -> photoGridList.remove(grid));
            });
            tabPane.getTabs().add(newTab);
            tabPane.getSelectionModel().select(newTab);
            newTab.setContent(indicatorContainer);
        }else {
            task.setOnSucceeded(e -> {
                photoTab.setContent(task.getValue());
                PhotoGrid grid = (PhotoGrid) task.getValue().getContent();
                grid.setOrigin(photoTab);
                photoTab.setText(currentInspection.getAddress());
            });
            photoTab.setContent(indicatorContainer);

        }
        new Thread(task).start();
    }


        private void initPhotoMain(CustomImage photo, PhotoGrid photoGrid){
            Platform.runLater(() -> {
                photo.getAdd().setOnAction(e -> setPhotosAssigned(true));
                photo.getRemove().setOnAction(e -> {
                    moveSelectionToGarbage(photo);
                    currentContext = null;
                });
                photo.getIncreaseZoom().setOnAction(e -> {
                    if (columnsPerPhotoGrid > 1) {
                        photo.getIncreaseZoom().setDisable(true);
                        photoGrid.setColumnCount(--columnsPerPhotoGrid);
                        for(PhotoGrid grid : photoGridList){
                            grid.setColumnCount(columnsPerPhotoGrid);
                        }
                        photo.getIncreaseZoom().setDisable(false);
                        currentContext = null;
                    }
                });

                photo.getDecreaseZoom().setOnAction(e -> {
                    if (columnsPerPhotoGrid < 5) {
                        photo.getDecreaseZoom().setDisable(true);
                        photoGrid.setColumnCount(++columnsPerPhotoGrid);
                        for(PhotoGrid grid : photoGridList){
                            grid.setColumnCount(columnsPerPhotoGrid);
                        }
                        photo.getDecreaseZoom().setDisable(false);
                        currentContext = null;
                    }
                });

                photo.getOpen().setOnAction(e -> {
                    new ImageViewer(photo);
                    currentContext = null;
                });

                photo.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode().equals(KeyCode.A) && event.isControlDown()) {
                        PhotoGrid grid = (PhotoGrid) photo.getParent();
                        for (Node n : grid.getChildren()) {
                            CustomImage custom = (CustomImage) n;
                            custom.setSelected(true);
                            if (!selectedPhotos.contains(custom)) selectedPhotos.add(custom);
                        }
                    }
                });
                setImageSizing(photo, false);
            });
        }
        private void initAssignedPhoto(CustomImage photo){
            photo.getRemoveAssigned().setOnAction(e -> {
                setPhotosAssigned(false);
                currentContext = null;
            });
            photo.getRemove2().setOnAction(e -> {
                setPhotosAssigned(false);
                currentContext = null;
            });
            photo.getOpen().setOnAction(e -> {
                new ImageViewer(photo);
                currentContext = null;
            });

            photo.addEventFilter(KeyEvent.KEY_PRESSED, event ->{
                if(event.getCode().equals(KeyCode.A) && event.isControlDown()){
                        for(Node n : assignedPhotosContainer.getChildren()){
                            CustomImage custom = (CustomImage) n;
                            custom.setSelected(true);
                            if(!assignedSelectedPhotos.contains(custom)){
                                assignedSelectedPhotos.add(custom);
                            }
                        }
                }
            });

            photo.setOwned(true);
            setImageSizing(photo, true);
        }
        private void setImageSizing(CustomImage photo, boolean assigned){
            ImageView image = photo.getImage();
            if(assigned){
                photo.prefHeightProperty().bind(assignedPhotosContainer.heightProperty());
                photo.getImage().fitHeightProperty().bind(selectedPhotosScroller.heightProperty().subtract(20));
            }else{
                double aspectRatio = image.imageProperty().get().getHeight() / image.imageProperty().get().getWidth();
                PhotoGrid grid = (PhotoGrid) photo.getParent();
                image.fitWidthProperty().bind(tabPane.widthProperty().divide(grid.getColumnCount()).subtract(
                        grid.vgapProperty().get() * (aspectRatio + 1) + (26/3.0)
                ));
            }
        }
        private void removeSelectionFromApp(CustomImage photo){
            if(photo.isOwned()){
                assignedPhotosContainer.getChildren().removeAll(assignedSelectedPhotos);
                assignedSelectedPhotos.stream().forEach(e -> currentInspection.removeImage(e));
                assignedSelectedPhotos.clear();
            }else{
                photo.getOrigin().getChildren().removeAll(selectedPhotos);
                selectedPhotos.clear();
                updatePhotoGrid(photo.getOrigin());
            }
        }
        private void moveSelectionToGarbage(CustomImage photo){
        if(photo.isOwned()){
            assignedPhotosContainer.getChildren().removeAll(assignedSelectedPhotos);
            assignedSelectedPhotos.stream().forEach(e -> {
                currentInspection.removeImage(e);
                trashList.add(e.getPath());
                e.setOwned(false);
            });
            drawPhotoGridTrash();
            assignedSelectedPhotos.clear();
        }else{
            PhotoGrid grid = (PhotoGrid) photo.getParent();
            if(grid.getOrigin() != trashTab){
                grid.getChildren().removeAll(selectedPhotos);
                selectedPhotos.forEach(e -> trashList.add(e.getPath()));
                updatePhotoGrid(grid);
            }else{
                selectedPhotos.forEach(e -> trashList.remove(e.getPath()));
            }
            drawPhotoGridTrash();
            selectedPhotos.clear();
        }
    }


        private void attachClickEvent(CustomImage photo, boolean assigned){
            photo.getImage().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                ContextMenu photoContext = photo.getPhotoContext();
                ContextMenu assignedPhotoContext = photo.getAssignedPhotoContext();
                ObservableList<CustomImage> selected = assigned ? assignedSelectedPhotos : selectedPhotos;

                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    if (currentContext != null) {
                        currentContext.hide();
                        currentContext = null;
                        event.consume();
                    } else {
                        if (photo.isSelected()) {
                            photo.setSelected(false);
                            selected.remove(photo);
                        } else {
                            photo.setSelected(true);
                            selected.add(photo);
                        }
                    }
                    photo.requestFocus();
                    System.out.println("--------------------------");
                    System.out.println("Owned: " + photo.isOwned());
                    System.out.println("Selected: " + photo.isSelected() + "\n");
                    System.out.println("Selected list: " );
                    for(CustomImage image : selectedPhotos){
                        System.out.println(image.getPath().getFileName());
                    }
                    System.out.println("Assigned selected list: " );
                    for(CustomImage image : assignedSelectedPhotos){
                        System.out.println(image.getPath().getFileName());
                    }
                    System.out.println("--------------------------\n");



                }
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    if (photo.isOwned()) {
                        assignedPhotoContext.show(photo, event.getScreenX(), event.getScreenY());
                        assignedPhotoContext.requestFocus();
                        currentContext = assignedPhotoContext;
                        if (assignedSelectedPhotos.size() == 0) {
                            photo.getRemoveAssigned().setDisable(true);
                            photo.getRemove2().setDisable(true);
                            photo.getDelete2().setDisable(true);
                        } else {
                            photo.getRemoveAssigned().setDisable(false);
                            photo.getRemove2().setDisable(false);
                            photo.getDelete2().setDisable(false);
                        }

                    } else {
                        photoContext.show(photo, event.getScreenX(), event.getScreenY());
                        photoContext.requestFocus();
                        currentContext = photoContext;
                        if (selectedPhotos.size() == 0) {
                            photo.getDelete().setDisable(true);
                            photo.getRemove().setDisable(true);
                            photo.getAdd().setDisable(true);
                        } else {
                            photo.getDelete().setDisable(false);
                            photo.getRemove().setDisable(false);
                            photo.getAdd().setDisable(false);
                        }
                    }
                }
                event.consume();
            });
        }
        private void setPhotosAssigned(boolean assigned) {
            if (assigned && selectedPhotos.size() > 0) {
                PhotoGrid grid = null;
                for(CustomImage photo : selectedPhotos){
                    grid = (PhotoGrid) photo.getParent();
                    currentInspection.assignImage(photo);
                    photo.setOwned(true);
                    grid.getChildren().remove(photo);
                }
                selectedPhotos.clear();
                assignedSelectedPhotos.clear();
                updatePhotoGrid(grid);
                updateSelectedPhotosContainer();
            }else{
                if (assignedSelectedPhotos.size() > 0) {
                    moveSelectionToGarbage(assignedSelectedPhotos.get(0));
                    selectedPhotos.clear();
                    assignedSelectedPhotos.clear();
                    updateSelectedPhotosContainer();
                }
            }
//                            ArrayList<CustomImage> list = photoGrid.getChildren()
//                                    .stream()
//                                    .filter(n -> n != null)
//                                    .map(n -> (CustomImage) n)
//                                    .collect(Collectors.toCollection(ArrayList::new));

//                            Collections.sort(list, (o1, o2) -> o1.getFileIndex() < o2.getFileIndex() ? -1 : o1.getFileIndex() == o2.getFileIndex() ? 0 : 1);
//                            photoGrid.getChildren().clear();
//                            photoGrid.getChildren().addAll(list)
            currentContext = null;
        }
        private void updateSelectedPhotosContainer(){
            assignedPhotosContainer.getChildren().clear();
            for(CustomImageProperties ip : currentInspection.getImageList()){
                CustomImage photo = new CustomImage(ip.getPath(), 0);
                attachClickEvent(photo, true);
                initAssignedPhoto(photo);
                assignedPhotosContainer.getChildren().add(photo);
            }
        }
        public void importInspections(){
            InspectionImporter importer = new InspectionImporter(stage);
            importer.setOnSucceeded(e -> {
                    if(importer.getValue() != null){
//                        setDisableControls(false);
                        importPhotos.setDisable(false);
                        assignedSelectedPhotos.clear();
                        selectedPhotos.clear();
                        assignedPhotosContainer.getChildren().clear();
                        ArrayList<Inspection> inspections = importer.getValue();
                        inspectionList = FXCollections.observableArrayList(inspections);
                        totalInspectionsLabel.setText(inspectionList.size() + " inspections complete");
                        currentInspection = inspections.get(0);
                        dataTable.setItems(inspectionList);
                        setFieldValues();
                        bindComponents();
                        populateDynamicCombos();
                        setAllFieldsAvailability();
                        populateCombos();
                        dataTab.setContent(null);
                        status.setVisible(true);
                        dataTable.getSelectionModel().selectFirst();
                        String info = "Successfuly Imported: " + importer.getNumberImported() + "\n" +
                                "Preexisting Records: " + importer.getNumberExisting() + "\n" +
                                "Excluded/Unsupported: " + importer.getNumberUnsupported() + "\n";

                        if (importer.getChangedInspectionType().length() > 0) {
                            info += "\n" +
                                    "The following inspections have been excluded due to altered service types: " +
                                    "\n" +
                                    importer.getChangedInspectionType();
                        }
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Import Operation Complete");
                        alert.setHeaderText("Import Summary:");
                        alert.setContentText(info);
                        alert.showAndWait();
                    }
                    dataTab.setContent(dataTable);
            });

            ProgressIndicator indicator = new ProgressIndicator(0);
            indicator.progressProperty().bind(importer.progressProperty());
            HBox indicatorContainer = new HBox(indicator);
            indicatorContainer.setAlignment(Pos.CENTER);
            indicator.prefHeightProperty().bind(tabPane.widthProperty().divide(3.0));
            indicator.prefWidthProperty().bind(tabPane.widthProperty().divide(3.0));
            indicator.maxWidthProperty().bind(tabPane.widthProperty().divide(3.0));
            indicator.maxHeightProperty().bind(tabPane.widthProperty().divide(3.0));
            dataTab.setContent(indicatorContainer);
            new Thread(importer).start();
        }
        private void updatePhotoGrid(PhotoGrid photoGrid){
            int columns = photoGrid.getColumnCount();
            ObservableList<Node> images = photoGrid.getChildren();
            Iterator<Node> iterator = images.iterator();
            int rows = (int) (Math.ceil((double)images.size()/columns));
            for(int r = 0; r < rows; r++){
                for(int c = 0; c < columns; c++){
                    if(iterator.hasNext()){
                        Node image = iterator.next();
                        if(image != null) {
                            setColumnIndex(image, c);
                            setRowIndex(image, r);
                        }
                    }
                }
            }
        }
        private void redrawPhotoGrid(PhotoGrid photoGrid){
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    int columns = photoGrid.getColumnCount();
                    ObservableList<Node> photos = photoGrid.getChildren();
                    Iterator<Node> iterator = photos.iterator();
                    int rows = (int) (Math.ceil((double)photos.size()/columns));
                    int counter = 0;
                    for(int r = 0; r < rows; r++){
                        for(int c = 0; c < columns; c++){
                            if(iterator.hasNext()){
                                counter++;
                                Node n = iterator.next();
                                if(n != null) {
                                    CustomImage photo = (CustomImage) n;
                                    ImageView i = new ImageView(new Image(photo.getPath().toUri().toString(), 350, 0, true, false));
                                    photo.setImage(i);
                                    setImageSizing(photo, false);
                                    attachClickEvent(photo, false);
                                    setColumnIndex(photo, c);
                                    setRowIndex(photo, r);
                                    updateProgress((double) counter, (double) photos.size());
                                }
                            }
                        }
                    }
                    System.gc();
                    return null;
                }
            };

            task.setOnSucceeded(e -> {
                ScrollPane scroll = new ScrollPane(photoGrid);
                photoGrid.getOrigin().setContent(scroll);
            });
            ProgressIndicator indicator = new ProgressIndicator(0);
            indicator.progressProperty().bind(task.progressProperty());
            HBox indicatorContainer = new HBox(indicator);
            indicatorContainer.setAlignment(Pos.CENTER);
            indicator.prefHeightProperty().bind(tabPane.widthProperty().divide(3.0));
            indicator.prefWidthProperty().bind(tabPane.widthProperty().divide(3.0));
            indicator.maxWidthProperty().bind(tabPane.widthProperty().divide(3.0));
            indicator.maxHeightProperty().bind(tabPane.widthProperty().divide(3.0));
            photoGrid.getOrigin().setContent(indicatorContainer);
            new Thread(task).start();
            selectedPhotos.clear();
        }
        private boolean save(){
            if((currentInspection != null) && (currentInspection.isComplete())){
                InspectionSerializer serializer = new InspectionSerializer();
                serializer.serialize(currentInspection);
                return true;
            }
            return false;
        }
        private void bindComponents(){
            occupancyField.valueProperty().bindBidirectional(currentInspection.occupancyProperty());
            propertyTypeField.valueProperty().bindBidirectional(currentInspection.propertyTypeProperty());
            constructionTypeField.valueProperty().bindBidirectional(currentInspection.constructionTypeProperty());
            propertyConditionField.valueProperty().bindBidirectional(currentInspection.propertyConditionProperty());
            forSaleField.valueProperty().bindBidirectional(currentInspection.forSaleProperty());
            determinedByField.valueProperty().bindBidirectional(currentInspection.determinedByProperty());
            roofTypeField.valueProperty().bindBidirectional(currentInspection.roofTypeProperty());
            landscapeTypeField.valueProperty().bindBidirectional(currentInspection.landscapeTypeProperty());
            landscapeConditionField.valueProperty().bindBidirectional(currentInspection.landscapeConditionProperty());
            grassHeightField.valueProperty().bindBidirectional(currentInspection.grassHeightProperty());
            garageTypeField.valueProperty().bindBidirectional(currentInspection.garageTypeProperty());
            spokeWithField.valueProperty().bindBidirectional(currentInspection.spokeWithProperty());
            brokerNameField.textProperty().bindBidirectional(currentInspection.brokerNameProperty());
            brokerNumberField.textProperty().bindBidirectional(currentInspection.brokerNumberProperty());
            noAccessReasonCombo.valueProperty().bindBidirectional(currentInspection.noAccessReasonProperty());
    }
        private void unBindComponents(){
            occupancyField.valueProperty().unbindBidirectional(currentInspection.occupancyProperty());
            propertyTypeField.valueProperty().unbindBidirectional(currentInspection.propertyTypeProperty());
            constructionTypeField.valueProperty().unbindBidirectional(currentInspection.constructionTypeProperty());
            propertyConditionField.valueProperty().unbindBidirectional(currentInspection.propertyConditionProperty());
            forSaleField.valueProperty().unbindBidirectional(currentInspection.forSaleProperty());
            determinedByField.valueProperty().unbindBidirectional(currentInspection.determinedByProperty());
            roofTypeField.valueProperty().unbindBidirectional(currentInspection.roofTypeProperty());
            landscapeTypeField.valueProperty().unbindBidirectional(currentInspection.landscapeTypeProperty());
            landscapeConditionField.valueProperty().unbindBidirectional(currentInspection.landscapeConditionProperty());
            grassHeightField.valueProperty().unbindBidirectional(currentInspection.grassHeightProperty());
            garageTypeField.valueProperty().unbindBidirectional(currentInspection.garageTypeProperty());
            spokeWithField.valueProperty().unbindBidirectional(currentInspection.spokeWithProperty());
            brokerNameField.textProperty().unbindBidirectional(currentInspection.brokerNameProperty());
            brokerNumberField.textProperty().unbindBidirectional(currentInspection.brokerNumberProperty());
            noAccessReasonCombo.valueProperty().unbindBidirectional(currentInspection.noAccessReasonProperty());
        }
        private void setFieldValues(){
            setValidValue(occupancyField, currentInspection.getOccupancy());
            setValidValue(propertyTypeField, currentInspection.getPropertyType());
            setValidValue(constructionTypeField, currentInspection.getConstructionType());
            setValidValue(propertyConditionField, currentInspection.getPropertyCondition());
            setValidValue(forSaleField, currentInspection.getForSale());
            setValidValue(determinedByField, currentInspection.getDeterminedBy());
            setValidValue(roofTypeField, currentInspection.getRoofType());
            setValidValue(landscapeTypeField, currentInspection.getLandscapeType());
            setValidValue(landscapeConditionField, currentInspection.getLandscapeCondition());
            setValidValue(grassHeightField, currentInspection.getGrassHeight());
            setValidValue(spokeWithField, currentInspection.getSpokeWith());
            setValidValue(noAccessReasonCombo, currentInspection.getNoAccessReason());
            brokerNameField.setText(currentInspection.getBrokerName());
            brokerNumberField.setText(currentInspection.getBrokerNumber());
            occupancyIndicators.getSelectionModel().clearSelection();
            noAccessBox.setSelected(currentInspection.isNoAccess());
            autoCommentBox.setSelected(currentInspection.isAutoComment());
            customCommentField.setText(currentInspection.getCustomComment());
            for(String s : currentInspection.getOccupancyIndicatorList()){
                occupancyIndicators.getSelectionModel().select(s);
            }

        }

        private static void setValidValue(ComboBox comboBox, String value){
            ObservableList<String> options = comboBox.getItems();
            if ((value != null)  &&  (options != null)  &&  (options.size() > 0)){
                boolean isValid = false;
                for (Object item : options) {
                    if (value.equals(item)) {
                        isValid = true;
                        break;
                    }
                }
                if (isValid) {
                    comboBox.setValue(value);
                }
            }
        }

        private void disableStandardFields(){
            garageTypeField.setDisable(true);
            spokeWithField.setDisable(true);
        }

        private void enableStandardFields(){
            garageTypeField.setDisable(false);
            spokeWithField.setDisable(false);
        }

        private void enableWellsFargoFields(){
            roofTypeField.setDisable(false);
            landscapeTypeField.setDisable(false);
            landscapeConditionField.setDisable(false);
            grassHeightField.setDisable(false);
        }

        private void disableWellsFargoFields(){
            roofTypeField.setDisable(true);
            landscapeTypeField.setDisable(true);
            landscapeConditionField.setDisable(true);
            grassHeightField.setDisable(true);
        }

        private void enableGlobalFields(){
            occupancyField.setDisable(false);
            propertyTypeField.setDisable(false);
            constructionTypeField.setDisable(false);
            propertyConditionField.setDisable(false);
            forSaleField.setDisable(false);
            determinedByField.setDisable(false);
        }

        private void setAllFieldsAvailability(){
            Inspection.InspectionType type = currentInspection.getType();
            enableGlobalFields();
            switch(type){
                case EXTERIOR_CALL_BACK_INSPECTION_WF:
                    enableWellsFargoFields();
                    disableStandardFields();
                    break;
                case EXTERIOR_INSPECTION_WF:
                    enableWellsFargoFields();
                    disableStandardFields();
                    break;
                case NO_CONTACT_INSPECTION_WF:
                    enableWellsFargoFields();
                    disableStandardFields();
                    break;
                default:
                    disableWellsFargoFields();
                    enableStandardFields();
            }
            if (determinedByField.getValue() != null) {
                if(determinedByField.getValue().toLowerCase().contains("visual")){
                    occupancyIndicators.setDisable(false);
                }else{
                    occupancyIndicators.setDisable(true);
                    occupancyIndicators.getSelectionModel().clearSelection();
                }
                if(!currentInspection.isWellsFargo()) {
                    if (determinedByField.getValue().toLowerCase().contains("direct")) {
                        spokeWithField.setDisable(false);
                    } else {
                        spokeWithField.setDisable(true);
                        spokeWithField.getSelectionModel().clearSelection();
                    }
                }
            }
        }

        public void setBrokerFieldsAvailability(){
            String value = forSaleField.getValue();

            if((value != null) && (value.toLowerCase().contains("broker"))){
                brokerNameField.setDisable(false);
                brokerNumberField.setDisable(false);
            }else{
                brokerNameField.setDisable(true);
                brokerNumberField.setDisable(true);
            }
        }

        private void populateCombos(){

            propertyTypeField.getItems().addAll(
                    "Single Family",
                    "Duplex",
                    "Triplex",
                    "Four-Plex",
                    "Condo/Townhouse",
                    "Mobile Home",
                    "Vacant Land"
            );

            constructionTypeField.getItems().addAll(
                    "Frame",
                    "Brick/Block",
                    "Stucco",
                    "Vinyl/Aluminum Siding"
            );

            propertyConditionField.getItems().addAll(
                    "Good",
                    "Fair"
            );

            roofTypeField.getItems().addAll(
                    "Asphalt",
                    "Metal",
                    "Slate",
                    "Tile",
                    "Wood"
            );

            landscapeTypeField.getItems().addAll(
                    "Grass",
                    "No lawn",
                    "Desert Landscape",
                    "Bare Dirt",
                    "Dead Lawn",
                    "Snow Covered"
            );

            landscapeConditionField.getItems().addAll(
                    "Yard Maintained",
                    "Overgrown Weeds",
                    "Trees/Shrubs Need Trimming",
                    "Overgrown Vines",
                    "Leaves/Pine Needles Present"
            );

            grassHeightField.getItems().addAll(
                    "Under 4\"",
                    "4-12\"",
                    "Over 12\""
            );

            garageTypeField.getItems().addAll(
                    "Attached",
                    "Detached",
                    "Carport",
                    "None"
            );
        }

        private void populateDynamicCombos(){
            switch(currentInspection.getType()){
                case OCCUPANCY_INSPECTION_AFAS:
                    forSaleField.getItems().clear();
                    forSaleField.getItems().addAll(
                            "For Sale by Broker",
                            "For Sale by Owner",
                            "Not for Sale"
                    );
                    occupancyField.getItems().clear();
                    occupancyField.getItems().addAll(
                            "Occupied by Unknown Occupant",
                            "Occupied by Owner"
                    );
                    determinedByField.getItems().clear();
                    determinedByField.getItems().addAll(
                            "Direct Contact",
                            "Neighbor",
                            "Visual Observation"
                    );
                    spokeWithField.getItems().clear();
                    spokeWithField.getItems().addAll(
                            "Other Person at Property",
                            "Relative"
                    );
                    break;

                case NO_CONTACT_INSPECTION_AFAS:
                    forSaleField.getItems().clear();
                    forSaleField.getItems().addAll(
                            "For Sale by Broker",
                            "For Sale by Owner",
                            "Not for Sale"
                    );
                    occupancyField.getItems().clear();
                    occupancyField.getItems().addAll(
                            "Occupied by Unknown Occupant"
                    );
                    determinedByField.getItems().clear();
                    determinedByField.getItems().addAll(
                            "Visual Observation"
                    );
                    spokeWithField.getItems().clear();

                case NO_CONTACT_INSPECTION:
                    forSaleField.getItems().clear();
                    forSaleField.getItems().addAll(
                            "For Sale by Broker",
                            "For Sale by Owner",
                            "Not for Sale"
                    );
                    occupancyField.getItems().clear();
                    occupancyField.getItems().addAll(
                            "Occupied"
                            );
                    determinedByField.getItems().clear();
                    determinedByField.getItems().addAll(
                            "Visual Observation"
                    );
                    spokeWithField.getItems().clear();
                    break;

                case NO_CONTACT_INSPECTION_WF:
                    forSaleField.getItems().clear();
                    forSaleField.getItems().addAll(
                            "Broker",
                            "Owner",
                            "Property not for sale"
                    );
                    occupancyField.getItems().clear();
                    occupancyField.getItems().addAll(
                            "Occupied"
                    );
                    determinedByField.getItems().clear();
                    determinedByField.getItems().addAll(
                            "Visual Observation"
                    );
                    spokeWithField.getItems().clear();
                    break;

                default:
                    forSaleField.getItems().clear();
                    forSaleField.getItems().addAll(
                            "Broker",
                            "Owner",
                            "Property not for sale"
                    );
                    occupancyField.getItems().clear();
                    occupancyField.getItems().addAll(
                            "Occupied by Unknown Occupant",
                            "Occupied by Owner"
                    );
                    determinedByField.getItems().clear();
                    determinedByField.getItems().addAll(
                            "Direct Contact",
                            "Neighbor",
                            "Visual Observation"
                    );
                    spokeWithField.getItems().clear();
            }
        }

        private void setUpDataTable(){

            TableColumn serviceIdColumn = new TableColumn("Service ID");
            serviceIdColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(.15));
            serviceIdColumn.setCellValueFactory(new PropertyValueFactory<>("serviceId"));

            TableColumn propertyIdColoumn = new TableColumn("Property ID");
            propertyIdColoumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(.15));
            propertyIdColoumn.setCellValueFactory(new PropertyValueFactory<>("propertyId"));

            TableColumn addressColumn = new TableColumn("Address");
            addressColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(.40));
            addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));


            TableColumn photoColumn = new TableColumn("Photos");
            photoColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(.15));
            photoColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfPhotos"));

            TableColumn completionColumn = new TableColumn("Complete");
            completionColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(.15));
            completionColumn.setCellValueFactory(new PropertyValueFactory<>("complete"));

            completionColumn.setCellFactory(column -> {
                return new TableCell<Inspection, Boolean>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        if(item != null && item){
                            ImageView icon =new ImageView("complete.png");
                            icon.setPreserveRatio(true);
                            icon.setFitHeight(30);

                            setGraphic(icon);
                        }else{
                            setGraphic(null);
                        }
                    }
                };
            });

            completionColumn.setStyle("-fx-alignment: center");
            dataTable.getColumns().addAll(serviceIdColumn, propertyIdColoumn, addressColumn, photoColumn, completionColumn);
            dataTable.getSelectionModel().selectFirst();
            dataTable.prefWidthProperty().bind(tabPane.widthProperty());
            setVisible(false);
            dataTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    photoTab.setContent(null);
                    autoCommentBox.setDisable(false);
                    noAccessBox.setDisable(false);
                    occupancyIndicators.setDisable(true);
                    occupancyIndicatorsActive = false;
                    occupancyIndicators.getSelectionModel().clearSelection();
                    unBindComponents();
                    currentInspection = newSelection;
                    populateDynamicCombos();
                    setFieldValues();
                    bindComponents();
                    setAllFieldsAvailability();
                    updateSelectedPhotosContainer();
                    updateNumberComplete();
                    inspectionTypeLabel.setText("Service: " + currentInspection.getType().name());
                    addressLabel.setText("Address: " + currentInspection.getAddress());
                    for(String s : currentInspection.getOccupancyIndicatorList()){
                        occupancyIndicators.getSelectionModel().select(s);
                    }
                    occupancyIndicatorsActive = true;
                    if(currentInspection.isNoAccess()){
                        disableAndClearAll();
                    }
                    if(goProMode){
                        drawPhotoGrid(false);
                    }
                }
            });
        }
        private void fillFormDefault() {
            if (currentInspection != null) {
                disableAndClearAll();
                setAllFieldsAvailability();
                Inspection.InspectionType type = currentInspection.getType();
                propertyTypeField.getSelectionModel().select("Single Family");
                constructionTypeField.getSelectionModel().select("Stucco");
                propertyConditionField.getSelectionModel().select("Good");
                forSaleField.getSelectionModel().select("Property not for sale");
                occupancyField.getSelectionModel().select("Occupied by Unknown Occupant");

                switch (type) {
                    case EXTERIOR_CALL_BACK_INSPECTION_WF:
                        determinedByField.getSelectionModel().select("Direct Contact");
                        roofTypeField.getSelectionModel().select("Slate");
                        landscapeTypeField.getSelectionModel().select("Grass");
                        landscapeConditionField.getSelectionModel().select("Yard Maintained");
                        grassHeightField.getSelectionModel().select("Under 4\"");
                        break;
                    case OCCUPANCY_INSPECTION_AFAS:
                        determinedByField.getSelectionModel().select("Visual Observation");
                        occupancyIndicators.getSelectionModel().clearSelection();
                        occupancyIndicators.getSelectionModel().select("Lawn Cut");
                        occupancyIndicators.getSelectionModel().select("Decorations");
                        garageTypeField.getSelectionModel().select("Attached");
                        break;
                    case NO_CONTACT_INSPECTION_AFAS:
                        determinedByField.getSelectionModel().select("Visual Observation");
                        occupancyIndicators.getSelectionModel().clearSelection();
                        occupancyIndicators.getSelectionModel().select("Lawn Cut");
                        occupancyIndicators.getSelectionModel().select("Decorations");
                        garageTypeField.getSelectionModel().select("Attached");
                        break;
                    default:
                        System.out.println("Can't fill form for " + type + ". (Not Implemented.)");
                }
            }
        }
}