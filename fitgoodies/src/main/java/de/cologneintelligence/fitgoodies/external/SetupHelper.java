package de.cologneintelligence.fitgoodies.external;

import java.util.LinkedList;
import java.util.List;

public class SetupHelper {
    private List<String> properties = new LinkedList<String>();

    public void addProperty(String property) {
        properties.add(property);
    }


    public List<String> getProperties() {
        return properties;
    }
}
