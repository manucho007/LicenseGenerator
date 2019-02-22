package ru.rtksoftlabs.licensegenerator;

import java.util.Map;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProtectedObject that = (ProtectedObject) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(components, that.components);
    }
}
