import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.file.Path;

public class Inspection implements Externalizable {
    public enum InspectionType {
        EXTERIOR_CALL_BACK_INSPECTION_WF("EXTERIOR CALL BACK INSPECTION (WF)"),
        EXTERIOR_INSPECTION_WF("EXTERIOR INSPECTION (WF)"),
        NO_CONTACT_INSPECTION_WF("NO CONTACT INSPECTION (WF)"),
        NO_CONTACT_INSPECTION("NO CONTACT INSPECTION"),
        NO_CONTACT_INSPECTION_AFAS("NO CONTACT INSPECTION (AFAS)"),
        OCCUPANCY_INSPECTION_AFAS("OCCUPANCY INSPECTION (AFAS)");

        String stringName;

        InspectionType(String stringName){
            this.stringName = stringName;
        }

        @Override
        public String toString(){
            return stringName;
        }
    }
    private transient ObservableList<CustomImageProperties> imageList = FXCollections.observableArrayList();
    private transient IntegerBinding numberOfPhotosBinding = Bindings.size(imageList);
    private transient IntegerProperty numberOfPhotos = new SimpleIntegerProperty(this, "numberOfPhotos",0);
    private transient BooleanProperty complete = new SimpleBooleanProperty(this, "complete", false);
    private transient ImageView completionImage = null;
    private InspectionType type = null;
    private final StringProperty propertyId = new SimpleStringProperty(this, "propertyId", null);
    private final StringProperty serviceId = new SimpleStringProperty(this, "serviceId", null);
    private final StringProperty address = new SimpleStringProperty(this, "address", null);
    private final StringProperty noAccessReason = new SimpleStringProperty(this, "noAccessReason", null);
    private final BooleanProperty callBackCard = new SimpleBooleanProperty(false);
    private final BooleanProperty noAccess = new SimpleBooleanProperty(false);
    private final BooleanProperty autoComment = new SimpleBooleanProperty(true);
    private final StringProperty customComment = new SimpleStringProperty(this, "customComment", null);
    private final StringProperty occupancy = new SimpleStringProperty(this, "occupancy", null);
    private final StringProperty propertyType = new SimpleStringProperty(this, "propertyType", null);
    private final StringProperty constructionType = new SimpleStringProperty(this, "constructionType", null);
    private final StringProperty propertyCondition = new SimpleStringProperty(this, "propertyCondition", null);
    private final StringProperty forSale = new SimpleStringProperty(this, "forSale", null);
    private final StringProperty determinedBy = new SimpleStringProperty(this, "determinedBy", null);
    private final StringProperty brokerName = new SimpleStringProperty(this, "brokerName", null);
    private final StringProperty brokerNumber = new SimpleStringProperty(this, "brokerNumber", null);
    private final StringProperty roofType = new SimpleStringProperty(this, "roofType", null);
    private final StringProperty landscapeType = new SimpleStringProperty(this, "landscapeType", null);
    private final StringProperty landscapeCondition = new SimpleStringProperty(this, "landscapeCondition", null);
    private final StringProperty grassHeight = new SimpleStringProperty(this, "grassHeight", null);
    private final StringProperty garageType = new SimpleStringProperty(this, "garageType", null);
    private final StringProperty spokeWith = new SimpleStringProperty(this, "spokeWith", null);
    private transient ObservableList<String> occupancyIndicatorList = FXCollections.observableArrayList();


    public boolean isWellsFargo(){
        switch(getType()){
            case EXTERIOR_CALL_BACK_INSPECTION_WF:
                return true;
            case EXTERIOR_INSPECTION_WF:
                return true;
            case NO_CONTACT_INSPECTION:
                return true;
            default:
                return false;
        }
    }

    public Inspection(String propertyId, String serviceId, String address, String type) {
        this.propertyId.set(propertyId);
        this.serviceId.set(serviceId);
        this.address.set(address);
        this.type = getInspectionTypeEnum(type);
        numberOfPhotos.bind(numberOfPhotosBinding);
    }
//
//    public void addIndicators(String ... indicators){
//        occupancyIndicatorList.clear();
//    }


    public String getNoAccessReason() {
        return noAccessReason.get();
    }

    public StringProperty noAccessReasonProperty() {
        return noAccessReason;
    }

    public void setNoAccessReason(String noAccessReason) {
        this.noAccessReason.set(noAccessReason);
    }

    public boolean isCallBackCard() {
        return callBackCard.get();
    }

    public BooleanProperty callBackCardProperty() {
        return callBackCard;
    }

    public void setCallBackCard(boolean callBackCard) {
        this.callBackCard.set(callBackCard);
    }

    public boolean isNoAccess() {
        return noAccess.get();
    }

    public BooleanProperty noAccessProperty() {
        return noAccess;
    }

    public void setNoAccess(boolean noAccess) {
        this.noAccess.set(noAccess);
    }

    public boolean isAutoComment() {
        return autoComment.get();
    }

    public BooleanProperty autoCommentProperty() {
        return autoComment;
    }

    public void setAutoComment(boolean autoComment) {
        this.autoComment.set(autoComment);
    }

    public String getCustomComment() {
        return customComment.get();
    }

    public StringProperty customCommentProperty() {
        return customComment;
    }

    public void setCustomComment(String customComment) {
        this.customComment.set(customComment);
    }

    public void setType(InspectionType type) {
        this.type = type;
    }

    public void setLandscapeCondition(String landscapeCondition) {
        this.landscapeCondition.set(landscapeCondition);
    }

    public ObservableList<String> getOccupancyIndicatorList() {
        return occupancyIndicatorList;
    }

    public void setOccupancyIndicatorList(ObservableList<String> list){
        occupancyIndicatorList.clear();
        occupancyIndicatorList.addAll(list);
    }

    public int getNumberOfPhotos() {
        return numberOfPhotos.get();
    }

    public IntegerProperty numberOfPhotosProperty() {
        return numberOfPhotos;
    }

    public void setNumberOfPhotos(int numberOfPhotos) {
        this.numberOfPhotos.set(numberOfPhotos);
    }

    public Number getNumberOfPhotosBinding() {
        return numberOfPhotosBinding.get();
    }

    public IntegerBinding numberOfPhotosBindingProperty() {
        return numberOfPhotosBinding;
    }

    public ObservableList<CustomImageProperties> getImageList() {
        return imageList;
    }

    public void assignImage(CustomImage image){
        Path path = image.getPath();
        boolean contains = false;
        for(CustomImageProperties p : imageList){
            if(p.getPath().equals(path)){
                contains = true;
                break;
            }
        }
        if(!contains)
            imageList.add(new CustomImageProperties(path));
    }

    public void removeImage(CustomImage image){
        Path path = image.getPath();
        for(CustomImageProperties im : imageList){
            if(im.getPath().equals(path)){
                imageList.remove(im);
                break;
            }
        }
    }

    public Inspection(){
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address){
        this.address.set(address);
    }

    public ReadOnlyStringProperty addressProperty() {
        return address;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId.set(propertyId);
    }

    public InspectionType getType() {
        return type;
    }

    public String getOccupancy() {
        return occupancy.get();
    }

    public StringProperty occupancyProperty() {
        return occupancy;
    }

    public void setOccupancy(String occupancy) {
        this.occupancy.set(occupancy);
    }

    public String getPropertyType() {
        return propertyType.get();
    }

    public StringProperty propertyTypeProperty() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType.set(propertyType);
    }

    public String getPropertyCondition() {
        return propertyCondition.get();
    }

    public StringProperty propertyConditionProperty() {
        return propertyCondition;
    }

    public void setPropertyCondition(String propertyCondition) {
        this.propertyCondition.set(propertyCondition);
    }

    public String getForSale() {
        return forSale.get();
    }

    public StringProperty forSaleProperty() {
        return forSale;
    }

    public void setForSale(String forSale) {
        this.forSale.set(forSale);
    }

    public String getDeterminedBy() {
        return determinedBy.get();
    }

    public StringProperty determinedByProperty() {
        return determinedBy;
    }

    public void setDeterminedBy(String determinedBy) {
        this.determinedBy.set(determinedBy);
    }

    public String getBrokerName() {
        return brokerName.get();
    }

    public StringProperty brokerNameProperty() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName.set(brokerName);
    }

    public String getBrokerNumber() {
        return brokerNumber.get();
    }

    public StringProperty brokerNumberProperty() {
        return brokerNumber;
    }

    public void setBrokerNumber(String brokerNumber) {
        this.brokerNumber.set(brokerNumber);
    }

    public String getRoofType() {
        return roofType.get();
    }

    public StringProperty roofTypeProperty() {
        return roofType;
    }

    public void setRoofType(String roofType) {
        this.roofType.set(roofType);
    }

    public String getLandscapeType() {
        return landscapeType.get();
    }

    public StringProperty landscapeTypeProperty() {
        return landscapeType;
    }

    public void setLandscapeType(String landscapeType) {
        this.landscapeType.set(landscapeType);
    }

    public String getLandscapeCondition() {
        return landscapeCondition.get();
    }

    public StringProperty landscapeConditionProperty() {
        return landscapeCondition;
    }

    public void setLanscapeCondition(String lanscapeCondition) {
        this.landscapeCondition.set(lanscapeCondition);
    }

    public String getGrassHeight() {
        return grassHeight.get();
    }

    public StringProperty grassHeightProperty() {
        return grassHeight;
    }

    public void setGrassHeight(String grassHeight) {
        this.grassHeight.set(grassHeight);
    }

    public String getGarageType() {
        return garageType.get();
    }

    public StringProperty garageTypeProperty() {
        return garageType;
    }

    public void setGarageType(String garageType) {
        this.garageType.set(garageType);
    }

    public String getSpokeWith() {
        return spokeWith.get();
    }

    public StringProperty spokeWithProperty() {
        return spokeWith;
    }

    public void setSpokeWith(String spokeWith) {
        this.spokeWith.set(spokeWith);
    }

    /* propertyIdField Property */
    public String getPropertyId() {
        return propertyId.get();
    }

    public StringProperty propertyIdProperty() {
        return propertyId;
    }

    /* serviceId Property */
    public String getServiceId() {
        return serviceId.get();
    }

    public void setServiceId(String serviceId) {
        serviceIdProperty().set(serviceId);
    }

    @Contract(pure = true)
    private StringProperty serviceIdProperty() {
        return serviceId;
    }

    /* constructionType Property */
    public String getConstructionType() {
        return constructionType.get();
    }

    public void setConstructionType(String constructionType) {
        constructionTypeProperty().set(constructionType);
    }

    public StringProperty constructionTypeProperty() {
        return constructionType;
    }

    public boolean getComplete() {
        return complete.get();
    }

    public BooleanProperty completeProperty() {
        return complete;
    }

    private void setComplete(boolean complete) {
        this.complete.set(complete);
    }

    public ImageView getCompletionImage() {
        return completionImage;
    }

    public void setCompletionImage(ImageView completionImage) {
        this.completionImage = completionImage;
    }

    @Nullable
    private InspectionType getInspectionTypeEnum(String inspectionType){

        InspectionType temp;
        temp = null;

        for(InspectionType t : InspectionType.values()){
            if(t.toString().equals(inspectionType)){
                return t;
            }
        }
        return null;
    }

    private boolean globalFieldsComplete(){
        boolean check =
                (
                getOccupancy() != null &&
                getPropertyType() != null &&
                getConstructionType() != null &&
                getPropertyCondition() != null &&
                getForSale() != null &&
                getDeterminedBy() != null
                );
        if(check){
            if(getForSale().toLowerCase().contains("broker")){
                check = (getBrokerName() != null &&
                        getBrokerNumber() != null);
            }
        }
        if(check){
            if(getDeterminedBy().toLowerCase().contains("visual")){
                check = occupancyIndicatorList.size() > 0;
            }
        }
        return check;
    }

    private boolean standardFieldsComplete(){
        boolean check = getGarageType() != null;
        if(check) {
            if (getDeterminedBy().toLowerCase().contains("direct")) {
                check = getSpokeWith() != null;
            }
        }
        return check;
    }

    private boolean wellsFargoFieldsComplete(){
        return (
                getRoofType() != null &&
                getLandscapeType() != null &&
                getLandscapeCondition() != null &&
                getGrassHeight() != null
                );
    }

    public boolean isComplete(){
        boolean check = globalFieldsComplete();
        if(check) {
            switch (type) {
                case EXTERIOR_CALL_BACK_INSPECTION_WF:
                    check = wellsFargoFieldsComplete();
                    break;
                case EXTERIOR_INSPECTION_WF:
                    check = wellsFargoFieldsComplete();
                    break;
                case NO_CONTACT_INSPECTION_WF:
                    check = wellsFargoFieldsComplete();
                    break;
                default:
                    check = standardFieldsComplete();
            }
        }

        if(isNoAccess()){
            check = ((getCustomComment() != null) || isAutoComment()) && getNoAccessReason() != null;
        }else{
            check = check && ((getCustomComment() != null) || isAutoComment());
        }

        setComplete(check);
        return check;
    }

    @Override
    public String toString() {
        return "[propertyIdField=" + propertyId.get() +
                ", serviceId=" + serviceId.get() +
                ", address=" + address.get() +
                ", type=" + type.toString() +
                "]";
    }

    @Override
    public void writeExternal(ObjectOutput o) throws IOException {
        o.writeObject(propertyId.get());
        o.writeObject(serviceId.get());
        o.writeObject(address.get());
        o.writeObject(type);
        o.writeObject(occupancy.get());
        o.writeObject(propertyType.get());
        o.writeObject(constructionType.get());
        o.writeObject(propertyCondition.get());
        o.writeObject(forSale.get());
        o.writeObject(determinedBy.get());
        o.writeObject(brokerName.get());
        o.writeObject(brokerNumber.get());
        o.writeObject(roofType.get());
        o.writeObject(landscapeType.get());
        o.writeObject(landscapeCondition.get());
        o.writeObject(grassHeight.get());
        o.writeObject(garageType.get());
        o.writeObject(spokeWith.get());
    }

    @Override
    public void readExternal(ObjectInput o) throws IOException, ClassNotFoundException {
        propertyId.set((String) o.readObject());
        serviceId.set((String) o.readObject());
        address.set((String) o.readObject());
        type = ((InspectionType) o.readObject());
        occupancy.set((String) o.readObject());
        propertyType.set((String) o.readObject());
        constructionType.set((String) o.readObject());
        propertyCondition.set((String) o.readObject());
        forSale.set((String) o.readObject());
        determinedBy.set((String) o.readObject());
        brokerName.set((String) o.readObject());
        brokerNumber.set((String) o.readObject());
        roofType.set((String) o.readObject());
        landscapeType.set((String) o.readObject());
        landscapeCondition.set((String) o.readObject());
        grassHeight.set((String) o.readObject());
        garageType.set((String) o.readObject());
        spokeWith.set((String) o.readObject());
    }
}