    import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Mitch on 7/30/2016.
 */
class InspectionImporter extends Task<ArrayList<Inspection>> {

    private ArrayList<Inspection> inspections;
    private int numberExisting, numberUnsupported, numberImported;
    private String changedInspectionType;
    private Stage stage;
    private File sheet;

    public InspectionImporter(Stage stage) {
        numberExisting = 0;
        numberImported = 0;
        numberUnsupported = 0;
        changedInspectionType = "";
        inspections = null;
        this.stage = stage;

        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel Files" ,"*.xlsx", "*.xlsm", "*.xls", "*.csv");
        chooser.getExtensionFilters().add(extFilter);
        sheet = chooser.showOpenDialog(stage);
    }

    public ArrayList<Inspection> getImportedInspections(){
        return inspections;
    }

    public int getNumberExisting() {
        return numberExisting;
    }

    public int getNumberUnsupported() {
        return numberUnsupported;
    }

    public int getNumberImported() {
        return numberImported;
    }

    public String getChangedInspectionType() {
        return changedInspectionType;
    }

    @Override
    protected ArrayList<Inspection> call() throws Exception {

        if(sheet == null){
            return null;
        }

        String[] types = {
                "EXTERIOR CALL BACK INSPECTION (WF)",
                "EXTERIOR INSPECTION (WF)",
                "NO CONTACT INSPECTION",
                "NO CONTACT INSPECTION (AFAS)",
                "NO CONTACT INSPECTION (WF)",
                "OCCUPANCY INSPECTION (AFAS)"
        };

        InspectionSerializer serializer = new InspectionSerializer();
        inspections = new ArrayList<>();
        FileInputStream inputStream = new FileInputStream(sheet);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet spreadsheet = workbook.getSheetAt(0);

        Iterator<Row> rowCounter = spreadsheet.iterator();
        int totalRecords = -1;
        XSSFRow currentRow = null;
        while(rowCounter.hasNext()){
            currentRow = (XSSFRow) rowCounter.next();
            if(currentRow.getCell(0).toString() == null){
                break;
            }
            totalRecords++;
        }

        currentRow = null;
        Iterator<Row> rowIterator = spreadsheet.iterator();
        rowIterator.next();
        int recordIndex = 1;

        while (rowIterator.hasNext()){
            currentRow = (XSSFRow) rowIterator.next();
            recordIndex++;
            if(currentRow.getCell(0).toString() == null){
                break;
            }

            int serviceIdInt = (int) Double.parseDouble(currentRow.getCell(0).toString());
            int propertyIdInt = (int) Double.parseDouble(currentRow.getCell(1).toString());

            String propertyId = Integer.toString(propertyIdInt);
            String serviceId = Integer.toString(serviceIdInt);
            String inspectionType = currentRow.getCell(2).toString().trim();
            String address = currentRow.getCell(3).toString();


            boolean validType = false;

            for (String type : types) {
                if (type.equals(inspectionType)) {
                    validType = true;
                    break;
                }
            }

            if (validType) {
                numberImported++;
                if (serializer.exists(propertyId)){
                    System.out.println("Deserializing " + propertyId);
                    Inspection existing = serializer.deSerialize(propertyId);
                    if(existing.getType().toString().equals(inspectionType)) {
                        existing.setServiceId(serviceId);
                        existing.isComplete();
                        inspections.add(existing);
                        numberExisting++;
                    }else{
                        changedInspectionType += "Property " + propertyId +
                                " changed from type " + existing.getType() +
                                " to type " + inspectionType + "\n";
                    }
                } else {
                    Inspection i = new Inspection(propertyId, serviceId,
                            address, inspectionType);
                    i.isComplete();
                    inspections.add(i);

                }
            }else{
                numberUnsupported++;
            }
            updateProgress((double) recordIndex, (double) totalRecords);
        }
        inputStream.close();
        System.out.println("Imported address list successfully:\n");
        inspections.forEach(System.out::println);
        return inspections;
    }
}