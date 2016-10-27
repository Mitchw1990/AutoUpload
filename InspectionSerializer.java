import javafx.collections.FXCollections;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Mitch on 7/30/2016.
 */
class InspectionSerializer {

    public void serialize(final Inspection inspection){
        String fileName = inspection.getPropertyId() + ".ser";

        FileOutputStream fileOutput = null;
        ObjectOutputStream objectOutput = null;

        try{
            fileOutput = new FileOutputStream(fileName);
            objectOutput = new ObjectOutputStream(fileOutput);
            objectOutput.writeObject(inspection);
            ArrayList<String> list = new ArrayList<>(inspection.getOccupancyIndicatorList());
            objectOutput.writeObject(list);
            objectOutput.writeBoolean(inspection.isNoAccess());
            objectOutput.writeBoolean(inspection.isCallBackCard());
            objectOutput.writeBoolean(inspection.isAutoComment());
            objectOutput.writeObject(inspection.getCustomComment());
            objectOutput.writeObject(inspection.getNoAccessReason());
            objectOutput.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public boolean exists(String propertyId){
        File file = new File(propertyId + ".ser");
        return file.exists();
    }


    public Inspection deSerialize(final String propertyId) {
        String fileName = propertyId + ".ser";
        Inspection inspection = null;
        FileInputStream fileInput = null;
        ObjectInputStream objectInput = null;

        try {
            fileInput = new FileInputStream(fileName);
            objectInput = new ObjectInputStream(fileInput);
            try {
                inspection = (Inspection) objectInput.readObject();
                ArrayList<String> list = (ArrayList<String>) objectInput.readObject();
                inspection.setOccupancyIndicatorList(FXCollections.observableList(list));
                boolean noAccess = objectInput.readBoolean();
                boolean callBack = objectInput.readBoolean();
                boolean autoComm = objectInput.readBoolean();
                String customComment = (String) objectInput.readObject();
                String noAccessReason = (String) objectInput.readObject();
                inspection.setNoAccess(noAccess);
                inspection.setCallBackCard(callBack);
                inspection.setAutoComment(autoComm);
                inspection.setCustomComment(customComment);
                inspection.setNoAccessReason(noAccessReason);
            }catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            objectInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inspection;
    }
}
