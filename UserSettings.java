import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.prefs.Preferences;

/**
 * Created by mrrobot on 8/11/16.
 */
class UserSettings {

    private StringProperty username = new SimpleStringProperty(null);
    private StringProperty password = new SimpleStringProperty(null);
    private StringProperty autoSystemTray = new SimpleStringProperty(null);


    public UserSettings(){
        password.set(getPassword());
        username.set(getUsername());
    }

    public String getPassword() {
        Preferences prefs = Preferences.userNodeForPackage(UserSettings.class);
        String result = prefs.get("password", null);
        if (result != null) {
            return result;
        } else {
            return null;
        }
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String pword) {
        Preferences prefs = Preferences.userNodeForPackage(UserSettings.class);
        if(pword != null){
            prefs.put("password", pword);
        }
        password.set(pword);
    }


    public String getUsername() {
        Preferences prefs = Preferences.userNodeForPackage(UserSettings.class);
        String result = prefs.get("username", null);
        if (result != null) {
            return result;
        } else {
            return null;
        }
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String uname) {
        Preferences prefs = Preferences.userNodeForPackage(UserSettings.class);
        if(uname != null){
            prefs.put("username", uname);
        }
        password.set(uname);
    }

    public String getAutoSystemTray() {
        Preferences prefs = Preferences.userNodeForPackage(UserSettings.class);
        String result = prefs.get("autoSysTray", null);
        if (result != null) {
            return result;
        } else {
            return null;
        }
    }

    public StringProperty autoSystemTrayProperty() {
        return username;
    }

    public void setAutoSystemTray(Boolean bool) {
        Preferences prefs = Preferences.userNodeForPackage(UserSettings.class);
        prefs.put("autoSysTray", bool.toString() );
        password.set(bool.toString());
    }



}
