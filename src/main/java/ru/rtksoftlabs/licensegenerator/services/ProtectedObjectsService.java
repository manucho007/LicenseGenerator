package ru.rtksoftlabs.licensegenerator.services;

import ru.rtksoftlabs.licensegenerator.shared.ProtectedObject;

import java.util.List;

public interface ProtectedObjectsService {
    List<ProtectedObject> getProtectedObjects();
    void updateProtectedObjects();
}
