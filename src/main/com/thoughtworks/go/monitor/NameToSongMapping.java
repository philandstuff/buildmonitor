package com.thoughtworks.go.monitor;

import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class NameToSongMapping {

    private String type;

    public NameToSongMapping(String type) {
        this.type = type;
    }

    public String songForUser(List<String> users) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(type + "_userToSongMapping");
        for (String user : users) {
            try {
                return resourceBundle.getString(user);
            } catch (MissingResourceException e) {
            }
        }
        return resourceBundle.getString("default");
    }
}
