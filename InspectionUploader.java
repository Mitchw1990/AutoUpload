import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.IOException;

import static io.github.seleniumquery.SeleniumQuery.$;

class InspectionUploader {
    private InspectionDriver driver;
    private String date = null;
    private Inspection.InspectionType currentType = null;

    public InspectionUploader(String date) {
        this.date = date;
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setJavascriptEnabled(true);
        setDriver(new InspectionDriver(capabilities));
        getDriver().manage().window().setSize(new Dimension(1000, 3000));
        getDriver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        $.driver().use(getDriver());
    }

    public void login() {
        getDriver().manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        Dimension dimension = new Dimension(1000, 2000);
        getDriver().manage().window().setSize(dimension);
        $.url("http://prism.spectrumfsi.com/web/m/login");
        getDriver().findElement(By.name("j_username")).clear();
        getDriver().findElement(By.name("j_username")).sendKeys("socalinspections@yahoo.com");
        getDriver().findElement(By.name("j_password")).clear();
        getDriver().findElement(By.name("j_password")).sendKeys("1954Chevy%");
        getDriver().findElement(By.cssSelector("input.button.ui-btn-hidden")).click();
    }

    public void uploadPhoto(String path, String sid) {
        getDriver().navigate().refresh();
        getDriver().get("http://prism.spectrumfsi.com/web/m/service/select");
        getDriver().findElement(By.name("serviceId")).clear();
        getDriver().findElement(By.name("serviceId")).sendKeys(sid);
        getDriver().findElement(By.cssSelector("input.button.ui-btn-hidden")).click();
        getDriver().findElement(By.xpath("//button[@onclick='MobileServiceConfirm.goToDetailEntry();']")).click();
        getDriver().findElement(By.xpath("//button[@onclick='MobileServiceConfirm.goToServiceMenu();']")).click();
        getDriver().findElement(By.linkText("Take Pictures")).click();
        getDriver().navigate().refresh();
        WebElement uploadPhoto = getDriver().findElement(By.cssSelector("input[name='files']"));
        JavascriptExecutor executor = getDriver();
        executor.executeScript("arguments[0].removeAttribute('multiple')", uploadPhoto);
        uploadPhoto.sendKeys(path);
        getDriver().findElement(By.cssSelector("button.button.ui-btn-hidden")).click();
    }

    public boolean uploadForm(Inspection i) {
        if (i.isComplete()) {
            Inspection.InspectionType type = i.getType();
            switch (type) {
                case OCCUPANCY_INSPECTION_AFAS:
                    try {
                        return occupancyInspectionAFAS(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case NO_CONTACT_INSPECTION_AFAS:
                    try {
                        return noContactInspectionAFAS(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case EXTERIOR_CALL_BACK_INSPECTION_WF:
                    try {
                        return exteriorCallBackInspectionWF(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("Unimplemented type :" + type);
                    break;
            }
        }
        return false;
    }

    private void fillDateField(){
        getDriver().findElement(By.id("MobileServiceDetailDynamicFormEdit_datePerformedInput")).clear();
        getDriver().findElement(By.id("MobileServiceDetailDynamicFormEdit_datePerformedInput")).sendKeys(date);
    }

    private boolean navigateToForm(Inspection i) throws Exception {
        getDriver().get("http://prism.spectrumfsi.com/web/m/service/" + i.getServiceId() + "/detail/flow/form");
        String completed = "Detail information has already been submitted. " +
                "For any changes please contact your SFS contact.";


//        if(driver.findElement(By.tagName("p")).toString().contains("any changes")){
//            System.out.println(i + " is already completed.");
//            return false;
//        }
        if(!isUnfilled()){
            System.out.println("Pre-filled form detected.  Resetting...");
            if(resetForm(i)) {
                navigateToForm(i);
            }else{
                return false;
            }
        }
        return true;
    }
    private boolean exteriorCallBackInspectionWF(Inspection i) throws Exception {
        navigateToForm(i);
        fillDateField();
        new Select(getDriver().findElement(By.id("service.detailList4.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList5.preApprovedValue"))).selectByVisibleText("Yes");
        new Select(getDriver().findElement(By.id("service.detailList8.preApprovedValue"))).selectByVisibleText("Suburban");
        new Select(getDriver().findElement(By.id("service.detailList9.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList10.preApprovedValue"))).selectByVisibleText("Stable");
        new Select(getDriver().findElement(By.id("service.detailList11.preApprovedValue"))).selectByVisibleText(i.getPropertyType());
        new Select(getDriver().findElement(By.id("service.detailList13.preApprovedValue"))).selectByVisibleText(i.getRoofType());
        getDriver().findElement(By.xpath("//a[@id='service.detailList15.preApprovedValues-button']/span")).click();
        getDriver().findElement(By.linkText("None")).click();
        getDriver().findElement(By.xpath("//div[@id='service.detailList15.preApprovedValues-listbox']/div/a/span/span[2]")).click();
        new Select(getDriver().findElement(By.id("service.detailList16.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList18.preApprovedValue"))).selectByVisibleText("No");
        getDriver().findElement(By.xpath("//a[@id='service.detailList33.preApprovedValues-button']/span/span")).click();
        getDriver().findElement(By.linkText("None")).click();
        getDriver().findElement(By.xpath("//div[@id='service.detailList33.preApprovedValues-listbox']/div/a/span")).click();
        new Select(getDriver().findElement(By.id("service.detailList36.preApprovedValue"))).selectByVisibleText(i.getPropertyCondition());
        new Select(getDriver().findElement(By.id("service.detailList39.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList40.preApprovedValue"))).selectByVisibleText("None");
        getDriver().findElement(By.xpath("//a[@id='service.detailList42.preApprovedValues-button']/span/span")).click();
        getDriver().findElement(By.xpath("(//a[contains(text(),'None')])[3]")).click();
        getDriver().findElement(By.xpath("//div[@id='service.detailList42.preApprovedValues-listbox']/div/a/span/span[2]")).click();
        getDriver().findElement(By.xpath("//a[@id='service.detailList44.preApprovedValues-button']/span")).click();
        getDriver().findElement(By.xpath("(//a[contains(text(),'None')])[4]")).click();
        getDriver().findElement(By.xpath("//div[@id='service.detailList44.preApprovedValues-listbox']/div/a/span/span[2]")).click();
        new Select(getDriver().findElement(By.id("service.detailList45.preApprovedValue"))).selectByVisibleText(i.getConstructionType());
        if(!fillForSaleFields(i, 47)) {
            System.out.println("Error filling for sale fields.");
            return false;
        }
        getDriver().findElement(By.xpath("//a[@id='service.detailList50.preApprovedValues-button']/span")).click();
        getDriver().findElement(By.xpath("(//a[contains(text(),'None')])[5]")).click();
        getDriver().findElement(By.xpath("//div[@id='service.detailList50.preApprovedValues-listbox']/div/a/span/span[2]")).click();
        new Select(getDriver().findElement(By.id("service.detailList69.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList70.preApprovedValue"))).selectByVisibleText(i.getOccupancy());
        getDriver().findElement(By.xpath("//a[@id='service.detailList72.preApprovedValues-button']/span/span")).click();
        getDriver().findElement(By.linkText(i.getDeterminedBy())).click();
        getDriver().findElement(By.xpath("//div[@id='service.detailList72.preApprovedValues-listbox']/div/a/span/span[2]")).click();
        if(i.getDeterminedBy().toLowerCase().contains("visual")){
            getDriver().findElement(By.xpath("//a[@id='service.detailList74.preApprovedValues-button']/span")).click();
            i.getOccupancyIndicatorList().stream().filter(indicator -> indicator != null).forEach(indicator ->
                    getDriver().findElement(By.linkText(indicator)).click());
            getDriver().findElement(By.xpath("//div[@id='service.detailList74.preApprovedValues-listbox']/div/a/span/span[2]")).click();
        }
        new Select(getDriver().findElement(By.id("service.detailList126.preApprovedValue"))).selectByVisibleText(i.getLandscapeType());
        new Select(getDriver().findElement(By.id("service.detailList128.preApprovedValue"))).selectByVisibleText(i.getGrassHeight());
        new Select(getDriver().findElement(By.id("service.detailList129.preApprovedValue"))).selectByVisibleText(i.getLandscapeCondition());
        getDriver().findElement(By.xpath("//a[@id='service.detailList130.preApprovedValues-button']/span/span")).click();
        getDriver().findElement(By.xpath("(//a[contains(text(),'None')])[7]")).click();
        getDriver().findElement(By.xpath("//div[@id='service.detailList130.preApprovedValues-listbox']/div/a/span/span[2]")).click();
        new Select(getDriver().findElement(By.id("service.detailList136.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList137.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList139.preApprovedValue"))).selectByVisibleText("Yes");
        getDriver().findElement(By.id("service.preApprovedCompletionNotes")).clear();
        String comment = i.isAutoComment() ? StringHelper.generateComment(i) : i.getCustomComment();
        getDriver().findElement(By.id("service.preApprovedCompletionNotes")).sendKeys(comment);
        return submit(i);
    }

    private boolean noContactInspectionAFAS(Inspection i) throws Exception{
        navigateToForm(i);
        fillDateField();
        new Select(getDriver().findElement(By.id("service.detailList0.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList4.preApprovedValue"))).selectByVisibleText("Yes");
        new Select(getDriver().findElement(By.id("service.detailList7.preApprovedValue"))).selectByVisibleText("Stable");
        new Select(getDriver().findElement(By.id("service.detailList8.preApprovedValue"))).selectByVisibleText("At");
        new Select(getDriver().findElement(By.id("service.detailList9.preApprovedValue"))).selectByVisibleText(i.getPropertyType());
        new Select(getDriver().findElement(By.id("service.detailList15.preApprovedValue"))).selectByVisibleText(i.getConstructionType());
        if(!fillForSaleFields(i, 17)){
            System.out.println("Error filling for sale fields.");
            return false;
        }
        new Select(getDriver().findElement(By.id("service.detailList20.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList21.preApprovedValue"))).selectByVisibleText(i.getOccupancy());
        getDriver().findElement(By.xpath("//a[@id='service.detailList22.preApprovedValues-button']/span/span")).click();
        i.getOccupancyIndicatorList().stream().filter(indicator -> indicator != null).forEach(indicator ->
                getDriver().findElement(By.linkText(indicator)).click());
        getDriver().findElement(By.xpath("//div[@id='service.detailList22.preApprovedValues-listbox']/div/a/span/span[2]")).click();
        new Select(getDriver().findElement(By.id("service.detailList28.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList34.preApprovedValue"))).selectByVisibleText("0");
        new Select(getDriver().findElement(By.id("service.detailList35.preApprovedValue"))).selectByVisibleText("0");
        new Select(getDriver().findElement(By.id("service.detailList36.preApprovedValue"))).selectByVisibleText("0");
        new Select(getDriver().findElement(By.id("service.detailList37.preApprovedValue"))).selectByVisibleText("0");
        new Select(getDriver().findElement(By.id("service.detailList38.preApprovedValue"))).selectByVisibleText("0");
        new Select(getDriver().findElement(By.id("service.detailList39.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList40.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList43.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList44.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList45.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList48.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList50.preApprovedValue"))).selectByVisibleText(i.getGarageType());
        new Select(getDriver().findElement(By.id("service.detailList51.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList52.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList53.preApprovedValue"))).selectByVisibleText("No");
        getDriver().findElement(By.xpath("//a[@id='service.detailList54.preApprovedValues-button']/span")).click();
        getDriver().findElement(By.linkText("N/A")).click();
        getDriver().findElement(By.xpath("//div[@id='service.detailList54.preApprovedValues-listbox']/div/a/span")).click();
        new Select(getDriver().findElement(By.id("service.detailList59.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList61.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList62.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList63.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList64.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList65.preApprovedValue"))).selectByVisibleText("No");
        if(i.getGarageType().toLowerCase().contains("det")){
            new Select(getDriver().findElement(By.id("service.detailList66.preApprovedValue"))).selectByVisibleText("Yes");
            getDriver().findElement(By.xpath("//a[@id='service.detailList67.preApprovedValues-button']/span")).click();
            getDriver().findElement(By.linkText("N/A")).click();
            getDriver().findElement(By.xpath("//div[@id='service.detailList67.preApprovedValues-listbox']/div/a/span")).click();
            getDriver().findElement(By.xpath("//a[@id='service.detailList68.preApprovedValues-button']/span")).click();
            getDriver().findElement(By.linkText("N/A")).click();
            getDriver().findElement(By.xpath("//div[@id='service.detailList68.preApprovedValues-listbox']/div/a/span")).click();
            getDriver().findElement(By.xpath("//a[@id='service.detailList69.preApprovedValues-button']/span")).click();
            getDriver().findElement(By.linkText("N/A")).click();
            getDriver().findElement(By.xpath("//div[@id='service.detailList69.preApprovedValues-listbox']/div/a/span")).click();
            getDriver().findElement(By.xpath("//a[@id='service.detailList70.preApprovedValues-button']/span")).click();
            getDriver().findElement(By.linkText("N/A")).click();
            getDriver().findElement(By.xpath("//div[@id='service.detailList70.preApprovedValues-listbox']/div/a/span")).click();
            getDriver().findElement(By.xpath("//a[@id='service.detailList71.preApprovedValues-button']/span")).click();
            getDriver().findElement(By.linkText("N/A")).click();
            getDriver().findElement(By.xpath("//div[@id='service.detailList71.preApprovedValues-listbox']/div/a/span")).click();
            getDriver().findElement(By.xpath("//a[@id='service.detailList72.preApprovedValues-button']/span")).click();
            getDriver().findElement(By.linkText("N/A")).click();
            getDriver().findElement(By.xpath("//div[@id='service.detailList72.preApprovedValues-listbox']/div/a/span")).click();
            getDriver().findElement(By.xpath("//a[@id='service.detailList73.preApprovedValues-button']/span")).click();
            getDriver().findElement(By.linkText("N/A")).click();
            getDriver().findElement(By.xpath("//div[@id='service.detailList73.preApprovedValues-listbox']/div/a/span")).click();
        }else{
            new Select(getDriver().findElement(By.id("service.detailList66.preApprovedValue"))).selectByVisibleText("No");
        }
        new Select(getDriver().findElement(By.id("service.detailList75.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList77.preApprovedValue"))).selectByVisibleText("No");
        getDriver().findElement(By.xpath("//a[@id='service.detailList80.preApprovedValues-button']/span/span")).click();
        getDriver().findElement(By.linkText("None")).click();
        getDriver().findElement(By.xpath("//div[@id='service.detailList80.preApprovedValues-listbox']/div/a/span")).click();
        new Select(getDriver().findElement(By.id("service.detailList83.preApprovedValue"))).selectByVisibleText("No");
        getDriver().findElement(By.xpath("//a[@id='service.detailList91.preApprovedValues-button']/span")).click();
        getDriver().findElement(By.linkText("N/A")).click();
        getDriver().findElement(By.xpath("//div[@id='service.detailList91.preApprovedValues-listbox']/div/a/span")).click();
        new Select(getDriver().findElement(By.id("service.detailList92.preApprovedValue"))).selectByVisibleText("Unknown");
        new Select(getDriver().findElement(By.id("service.detailList93.preApprovedValue"))).selectByVisibleText("Unknown");
        new Select(getDriver().findElement(By.id("service.detailList94.preApprovedValue"))).selectByVisibleText("Unknown");
        getDriver().findElement(By.id("service.detailList96.preApprovedValue"))
                .sendKeys("The property is in " + i.getPropertyCondition()+ " condition.");
        fillPropertyConditionFieldAFAS(i, 95);
        getDriver().findElement(By.id("service.preApprovedCompletionNotes")).clear();
        String comment = i.isAutoComment() ? StringHelper.generateComment(i) : i.getCustomComment();
        getDriver().findElement(By.id("service.preApprovedCompletionNotes")).sendKeys(comment);
        return submit(i);
    }

    private boolean fillForSaleFields(Inspection i , int fieldNum){
        Select listBox = new Select(getDriver().findElement(By.id("service.detailList" + fieldNum + ".preApprovedValue")));
        if (listBox != null) {
            String keyWord = "not";
            if (i.getForSale().toLowerCase().contains("owner")) {
                keyWord = "owner";
            } else if (i.getForSale().toLowerCase().contains("broker")) {
                keyWord = "broker";
                String phoneNumber = StringHelper.formatAsPhoneNumber(i.getBrokerNumber());
                if (phoneNumber == null) {
                    System.out.println("Error: Unable to format string to phone number.");
                    return false;
                }else{
                    getDriver().findElement(By.id("service.detailList" + ++fieldNum + ".preApprovedValue")).clear();
                    getDriver().findElement(By.id("service.detailList" + fieldNum + ".preApprovedValue")).sendKeys(i.getBrokerName());
                    getDriver().findElement(By.id("service.detailList" + ++fieldNum + ".preApprovedValue")).clear();
                    getDriver().findElement(By.id("service.detailList" + fieldNum + ".preApprovedValue")).sendKeys(phoneNumber);
                }
            }
            for (WebElement option : listBox.getOptions()) {
                if (option.getText().toLowerCase().contains(keyWord)) {
                    listBox.selectByVisibleText(option.getText());
                    break;
                }
            }
            return true;
        }
        return false;
    }

    private boolean isUnfilled(){
        return ((driver.findElement(By.id("MobileServiceDetailDynamicFormEdit_datePerformedInput")).getAttribute("value").length() > 0)
                && (driver.findElement(By.id("service.preApprovedCompletionNotes")).getAttribute("value").length() > 0));
    }

    private boolean occupancyInspectionAFAS(Inspection i) throws Exception{
        navigateToForm(i);
        fillDateField();
        new Select(getDriver().findElement(By.id("service.detailList0.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList4.preApprovedValue"))).selectByVisibleText("Yes");
        new Select(getDriver().findElement(By.id("service.detailList7.preApprovedValue"))).selectByVisibleText("Stable");
        new Select(getDriver().findElement(By.id("service.detailList8.preApprovedValue"))).selectByVisibleText("At");
        new Select(getDriver().findElement(By.id("service.detailList9.preApprovedValue"))).selectByVisibleText(i.getPropertyType());
        new Select(getDriver().findElement(By.id("service.detailList15.preApprovedValue"))).selectByVisibleText(i.getConstructionType());
        if(!fillForSaleFields(i, 17)){
            System.out.println("Error filling for sale fields.");
            return false;
        }
        new Select(getDriver().findElement(By.id("service.detailList20.preApprovedValue"))).selectByVisibleText(i.getOccupancy());
        getDriver().findElement(By.xpath("//a[@id='service.detailList21.preApprovedValues-button']/span/span[2]")).click();
        getDriver().findElement(By.linkText(i.getDeterminedBy())).click();
        getDriver().findElement(By.xpath("//div[@id='service.detailList21.preApprovedValues-listbox']/div/a/span/span[2]")).click();
        if (i.getDeterminedBy().toLowerCase().contains("visual")) {
            for (String indicator : i.getOccupancyIndicatorList()) {
                getDriver().findElement(By.xpath("//a[@id='service.detailList23.preApprovedValues-button']/span/span")).click();
                getDriver().findElement(By.linkText(indicator)).click();
            }
            getDriver().findElement(By.xpath("//div[@id='service.detailList23.preApprovedValues-listbox']/div/a/span/span[2]")).click();
        }else{
            getDriver().findElement(By.xpath("//a[@id='service.detailList24.preApprovedValues-button']/span/span")).click();
            if (i.getDeterminedBy().toLowerCase().contains("neighbor")) {
                getDriver().findElement(By.partialLinkText("Neighbor")).click();
                getDriver().findElement(By.xpath("//div[@id='service.detailList24.preApprovedValues-listbox']/div/a/span/span[2]")).click();
            }else{
                getDriver().findElement(By.linkText(i.getSpokeWith())).click();
                getDriver().findElement(By.xpath("//div[@id='service.detailList24.preApprovedValues-listbox']/div/a/span/span[2]")).click();
                getDriver().findElement(By.id("service.detailList25.preApprovedValue")).clear();
                getDriver().findElement(By.id("service.detailList25.preApprovedValue")).sendKeys("Unknown");
                getDriver().findElement(By.id("service.detailList26.preApprovedValue")).clear();
                getDriver().findElement(By.id("service.detailList26.preApprovedValue")).sendKeys("(000) 000-0000");
            }
            new Select(getDriver().findElement(By.id("service.detailList27.preApprovedValue"))).selectByVisibleText("Other No.");
            new Select(getDriver().findElement(By.id("service.detailList28.preApprovedValue"))).selectByVisibleText("Indifferent");
        }
        new Select(getDriver().findElement(By.id("service.detailList33.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList39.preApprovedValue"))).selectByVisibleText("0");
        new Select(getDriver().findElement(By.id("service.detailList40.preApprovedValue"))).selectByVisibleText("0");
        new Select(getDriver().findElement(By.id("service.detailList41.preApprovedValue"))).selectByVisibleText("0");
        new Select(getDriver().findElement(By.id("service.detailList42.preApprovedValue"))).selectByVisibleText("0");
        new Select(getDriver().findElement(By.id("service.detailList43.preApprovedValue"))).selectByVisibleText("0");
        new Select(getDriver().findElement(By.id("service.detailList44.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList45.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList48.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList49.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList50.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList53.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList55.preApprovedValue"))).selectByVisibleText(i.getGarageType());
        new Select(getDriver().findElement(By.id("service.detailList56.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList57.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList58.preApprovedValue"))).selectByVisibleText("No");
        getDriver().findElement(By.xpath("//a[@id='service.detailList59.preApprovedValues-button']/span")).click();
        getDriver().findElement(By.linkText("N/A")).click();
        getDriver().findElement(By.xpath("//div[@id='service.detailList59.preApprovedValues-listbox']/div/a/span")).click();
        new Select(getDriver().findElement(By.id("service.detailList64.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList66.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList67.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList68.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList69.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList70.preApprovedValue"))).selectByVisibleText("No");
        if (i.getGarageType().equals("Detached")) {
            new Select(getDriver().findElement(By.id("service.detailList71.preApprovedValue"))).selectByVisibleText("Yes");
            getDriver().findElement(By.xpath("//a[@id='service.detailList72.preApprovedValues-button']/span/span")).click();
            getDriver().findElement(By.linkText("N/A")).click();
            getDriver().findElement(By.xpath("//div[@id='service.detailList72.preApprovedValues-listbox']/div/a/span/span[2]")).click();

            getDriver().findElement(By.xpath("//a[@id='service.detailList73.preApprovedValues-button']/span/span")).click();
            getDriver().findElement(By.linkText("N/A")).click();
            getDriver().findElement(By.xpath("//div[@id='service.detailList73.preApprovedValues-listbox']/div/a/span/span[2]")).click();

            getDriver().findElement(By.xpath("//a[@id='service.detailList74.preApprovedValues-button']/span/span")).click();
            getDriver().findElement(By.linkText("N/A")).click();
            getDriver().findElement(By.xpath("//div[@id='service.detailList74.preApprovedValues-listbox']/div/a/span/span[2]")).click();

            getDriver().findElement(By.xpath("//a[@id='service.detailList75.preApprovedValues-button']/span")).click();
            getDriver().findElement(By.linkText("N/A")).click();
            getDriver().findElement(By.xpath("//div[@id='service.detailList75.preApprovedValues-listbox']/div/a/span/span[2]")).click();

            getDriver().findElement(By.xpath("//a[@id='service.detailList76.preApprovedValues-button']/span/span")).click();
            getDriver().findElement(By.linkText("N/A")).click();
            getDriver().findElement(By.xpath("//div[@id='service.detailList76.preApprovedValues-listbox']/div/a/span/span[2]")).click();

            getDriver().findElement(By.xpath("//a[@id='service.detailList77.preApprovedValues-button']/span/span")).click();
            getDriver().findElement(By.linkText("N/A")).click();
            getDriver().findElement(By.xpath("//div[@id='service.detailList77.preApprovedValues-listbox']/div/a/span/span[2]")).click();

            getDriver().findElement(By.xpath("//a[@id='service.detailList78.preApprovedValues-button']/span")).click();
            getDriver().findElement(By.linkText("N/A")).click();
            getDriver().findElement(By.xpath("//div[@id='service.detailList78.preApprovedValues-listbox']/div/a/span/span[2]")).click();
        } else {
            new Select(getDriver().findElement(By.id("service.detailList71.preApprovedValue"))).selectByVisibleText("No");
        }
        new Select(getDriver().findElement(By.id("service.detailList80.preApprovedValue"))).selectByVisibleText("No");
        new Select(getDriver().findElement(By.id("service.detailList82.preApprovedValue"))).selectByVisibleText("No");
        getDriver().findElement(By.xpath("//a[@id='service.detailList85.preApprovedValues-button']/span/span")).click();
        getDriver().findElement(By.linkText("None")).click();
        getDriver().findElement(By.xpath("//div[@id='service.detailList85.preApprovedValues-listbox']/div/a/span")).click();
        new Select(getDriver().findElement(By.id("service.detailList89.preApprovedValue"))).selectByVisibleText("No");
        getDriver().findElement(By.xpath("//a[@id='service.detailList97.preApprovedValues-button']/span")).click();
        getDriver().findElement(By.linkText("Unknown")).click();
        getDriver().findElement(By.xpath("//div[@id='service.detailList97.preApprovedValues-listbox']/div/a/span")).click();
        new Select(getDriver().findElement(By.id("service.detailList98.preApprovedValue"))).selectByVisibleText("Unknown");
        new Select(getDriver().findElement(By.id("service.detailList99.preApprovedValue"))).selectByVisibleText("Unknown");
        new Select(getDriver().findElement(By.id("service.detailList100.preApprovedValue"))).selectByVisibleText("Unknown");
        fillPropertyConditionFieldAFAS(i, 101);
        getDriver().findElement(By.id("service.detailList102.preApprovedValue")).sendKeys("The property is in "
                + i.getPropertyCondition() + " condition.");
        String comment = i.isAutoComment() ? StringHelper.generateComment(i) : i.getCustomComment();
        getDriver().findElement(By.id("service.preApprovedCompletionNotes")).sendKeys(comment);
        return submit(i);
    }

    public void exteriorInspectionWF(Inspection i) {}

    public void noContactInspection(Inspection i) {}

    public void occupancyVerificationDMI(Inspection i) {}

    public void noContactInspectionDMI(Inspection i) {}

    public void takeScreenshot(Inspection i){
        File screenShot = getDriver().getScreenshotAs(OutputType.FILE);
        String newPic = String.format("%s/src/screenshots/" + i.getAddress() + ".png", System.getProperty("user.dir"));
        try{
            FileUtils.copyFile(screenShot, new File(newPic));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean submit(Inspection i){
        getDriver().executeScript("arguments[0].click();", getDriver().
                findElement(By.id("MobileServiceDetailDynamicFormEdit_saveSubmitButton")));

        Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriver())
                .withTimeout(10, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS);
        try{
            wait.until(d -> d != null ? d.getCurrentUrl().toLowerCase().contains("validate") : false);
            WebElement confirmation = wait.until(d -> d != null ? d.findElement(By.cssSelector("p")) : null);
            if(!confirmation.getText().contains("Success!")){
                System.out.println("Error: Unable to confirm submission form for " + i);
                return false;
            }
        }catch(Exception e){
            System.out.println("Error loading validation page for " +
                    i + "\n" + e.getMessage());
            return false;
        }

        try{
            WebElement confirmationButton = wait.until(d -> d != null ? d.findElement(By.id("MobileServiceDetailDynamicFormConfirmation_completeButton")) : null);
            getDriver().executeScript("arguments[0].click();", confirmationButton);
        }catch(Exception e){
            System.out.println("Error triggering confirmation button for " +
                    i + "\n" + e.getMessage());
            return false;
        }
        return true;
    }

    public Inspection.InspectionType getCurrentType() {
        return currentType;
    }

    private boolean resetForm(Inspection i) {
        driver.findElement(By.id("MobileServiceDetailDynamicFormEdit_resetButton")).click();
        try {
            Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriver())
                    .withTimeout(10, TimeUnit.SECONDS)
                    .pollingEvery(1, TimeUnit.SECONDS);
            wait.until(d -> d != null ? d.getCurrentUrl().toLowerCase().contains("reset") : false);
        }catch(Exception e){
            System.out.println("Error resetting pre-filled form for: "
                    + i + "\n" + e.getMessage());
            return false;
        }
        return true;
    }

    private boolean fillPropertyConditionFieldAFAS(Inspection i, int fieldNum){
        String condition = i.getPropertyCondition().toLowerCase();
        Select listBox = new Select(getDriver().findElement(By.id("service.detailList" + fieldNum + ".preApprovedValue")));
        for (WebElement option : listBox.getOptions()) {
            if (option.getText().toLowerCase().contains(condition)) {
                option.click();
                return true;
            }
        }
        System.out.println("Error: Unable to fill property condition combo for " + i);
        return false;
    }

    public void kill(){
        getDriver().quit();
        getDriver().close();
    }

    private void waitForVisibility(By by){
        Wait<WebDriver> wait = new WebDriverWait(getDriver(), 10);
        wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public static void main(String[] args) throws Exception {


    }


    private InspectionDriver getDriver() {
        return driver;
    }

    private void setDriver(InspectionDriver driver) {
        this.driver = driver;
    }
}
