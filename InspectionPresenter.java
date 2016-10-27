import javafx.stage.Stage;

class InspectionPresenter {
    private final InspectionView view;
    private final Stage stage;

    public InspectionPresenter(InspectionView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        attachEvents();
    }

    private void attachEvents() {
        view.importPhotos.setOnAction(e -> view.drawPhotoGrid(true));
        view.importSpreadsheet.setOnAction(e -> view.importInspections());
        view.forSaleField.setOnAction(e -> view.setBrokerFieldsAvailability());
        view.settings.setOnAction(e -> new SettingsPage());
        view.startUpload.setOnAction(e -> new UploadDialog(view.inspectionList, stage));
    }
}