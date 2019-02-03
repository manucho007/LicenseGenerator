package ru.rtksoftlabs.licensegenerator;

import java.util.ArrayList;

public class ProtectedObject {
    private String name;
    private ArrayList<String> components;

    public ProtectedObject() {
    }

    public ProtectedObject(String name, ArrayList<String> components) {
        this.name = name;
        this.components = components;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getComponents() {
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
