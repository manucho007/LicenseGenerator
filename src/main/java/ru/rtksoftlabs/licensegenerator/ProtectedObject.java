package ru.rtksoftlabs.licensegenerator;

import java.util.Map;

public class ProtectedObject {
    private String name;
    private Map<String, String> components;

    public ProtectedObject() {
    }

    public ProtectedObject(String name, Map<String, String> components) {
        this.name = name;
        this.components = components;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getComponents() {
        return components;
    }

    @Override
    public String toString() {
        return "ProtectedObject{" +
                "name='" + name + '\'' +
                ", components=" + components +
                '}';
    }
}
