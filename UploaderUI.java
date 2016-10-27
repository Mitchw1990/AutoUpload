import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Mitch on 8/21/2016.
 */
class UploaderUI extends GridPane {
    private ObservableList<Inspection> inspections;
    private ProgressBar indicator = new ProgressBar(0);
    private HBox container = new HBox(indicator);
    private Label statusLabel = new Label();
    private StringProperty status = new SimpleStringProperty(null);
    private String dateStamp;
    private Stage mainStage;

    UploaderUI(ObservableList<Inspection> inspections, Stage mainStage, String dateStamp){
        this.dateStamp = dateStamp;
        this.inspections = inspections;
        this.mainStage = mainStage;
        ColumnConstraints c0 = new ColumnConstraints();
        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();
        c0.setPercentWidth(10);
        c1.setPercentWidth(80);
        c2.setPercentWidth(10);
        RowConstraints r0 = new RowConstraints();
        RowConstraints r1 = new RowConstraints();
        RowConstraints r2 = new RowConstraints();
        RowConstraints r3 = new RowConstraints();
        RowConstraints r4 = new RowConstraints();
        RowConstraints r5 = new RowConstraints();
        r0.setPercentHeight(10);
        r1.setPercentHeight(20);
        r2.setPercentHeight(10);
        r3.setPercentHeight(30);
        r4.setPercentHeight(10);
        r4.setPercentHeight(10);
        r5.setPercentHeight(10);
        this.getColumnConstraints().addAll(c0,c1,c2);
        this.getRowConstraints().addAll(r0, r1, r2, r3, r4, r5);
        this.add(statusLabel, 1, 2);
        this.add(container, 1, 3);
        indicator.setMaxHeight(Double.MAX_VALUE);
        indicator.prefWidthProperty().bind(container.widthProperty());
    }

    public void uploadInspections(){
        final FileOutputStream[] fileOut = {null};
        int progressTotal = inspections.size();
        for(Inspection i : inspections){
            progressTotal += i.getImageList().size();
        }
        int finalProgressTotal = progressTotal;
        Task<String> task = new Task<String>(){
            @Override
            protected String call() throws Exception{
                XSSFWorkbook wb = new XSSFWorkbook();
                Sheet sheet = wb.createSheet("upload summary");
                InspectionUploader uploader = new InspectionUploader(dateStamp);
                uploader.login();
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("SID");
                header.createCell(1).setCellValue("PID");
                header.createCell(2).setCellValue("Address");
                header.createCell(3).setCellValue("Uploaded");
                header.createCell(4).setCellValue("Photos Uploaded");

                int counter = 0;
                int rowCounter = 0;
                for(Inspection i : inspections){
                    rowCounter++;
                    Row row = sheet.createRow(rowCounter);
                    row.createCell(0).setCellValue(i.getServiceId());
                    row.createCell(1).setCellValue(i.getPropertyId());
                    row.createCell(2).setCellValue(i.getAddress());
                    row.createCell(3);
                    row.createCell(4);
                    int photosUploaded = 0;
//                    if(i.isComplete()){
//                        updateStatus("Uploading SID" + i.getServiceId());
//                        if (uploader.uploadForm(i)){
//                            updateStatus("Succeed: SID" + i.getServiceId());
//                            row.getCell(3).setCellValue("Succeed");
//                        }else{
//                            updateStatus("Fail: SID" + i.getServiceId());
//                            row.getCell(3).setCellValue("Fail");
//                            Thread.sleep(3000);
//                            uploader.takeScreenshot(i);
//                        }
//                    }else{
//                        row.getCell(3).setCellValue("Incomplete");
//                    }
                    if(i.getImageList().size() > 0) {
                        updateStatus("Uploading photos for SID" + i.getServiceId());
                        for (CustomImageProperties image : i.getImageList()) {
                            updateProgress((double) ++counter, (double) finalProgressTotal);
                            updateStatus("Processing " + image.getPath().getFileName());
                            File tempFile = image.getPath().toFile();
                            String newPhoto = ImageHelper.resizeAndWatermark(tempFile, dateStamp);
                            updateStatus("Uploading " + image.getPath().getFileName());
                            boolean success = true;
                            try{
                                uploader.uploadPhoto(newPhoto, i.getServiceId());
                            }catch(Exception e){
                                success = false;
                                System.out.println("Error uploading photo: " + newPhoto + "\n");
                                e.printStackTrace();
                            }
                            if(success){
                                photosUploaded++;
                            }
                        }
                    System.gc();
                    }
                    ImageHelper.cleanUp();
                    row.getCell(4).setCellValue(photosUploaded);
                    if(photosUploaded != i.getImageList().size()){
                        CellStyle style = wb.createCellStyle();
                        style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
                        row.getCell(4).setCellStyle(style);
                    }
                    updateProgress((double) ++counter, (double) finalProgressTotal);
                    try {
                        fileOut[0] = new FileOutputStream(dateStamp.replace('/','.') + "_uploadSummary.xlsx", true);
                        wb.write(fileOut[0]);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
                if(fileOut[0] != null) {
                    fileOut[0].close();
                }
                uploader.kill();
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Upload Complete");
            alert.setHeaderText("Uploader has finished all operations.");
            alert.setContentText("See upload summary spreadsheet for details.");
            alert.showAndWait();
//            mainStage.close();
//            System.exit(0);
//            Platform.exit();
        });
        statusLabel.setText("Initializing...");
        indicator.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(statusProperty());
        new Thread(task).start();
    }

    private void updateStatus(String status){
        Platform.runLater(() -> setStatus(status));

    }

    public String getStatus() {
        return status.get();
    }

    private StringProperty statusProperty() {
        return status;
    }

    private void setStatus(String status) {
        this.status.set(status);
    }
}
