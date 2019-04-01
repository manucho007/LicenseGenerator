package ru.rtksoftlabs.licensegenerator.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.rtksoftlabs.LicenseCommons.services.ProtectedObjectsService;
import ru.rtksoftlabs.LicenseCommons.shared.ProtectedObject;
import ru.rtksoftlabs.LicenseCommons.shared.ProtectedObjects;
import ru.rtksoftlabs.licensegenerator.dao.ProtectedObjectsData;

import java.util.Map;
import java.util.TreeMap;

@Service
@Profile("!inno")
public class ProtectedObjectsServiceImpl implements ProtectedObjectsService {
    @Autowired
    private ProtectedObjectsData protectedObjectsData;

    @Override
    public ProtectedObjects getProtectedObjects() {
        ProtectedObjects returnProtectedObjects = new ProtectedObjects();

        for (ProtectedObjects protectedObjects: protectedObjectsData.getProtectedObjects().values()) {
            returnProtectedObjects.getObjects().putAll(protectedObjects.getObjects());
        }

        Map<String, ProtectedObject> sortedProtectedObjects = new TreeMap<>(returnProtectedObjects.getObjects());

        return new ProtectedObjects(sortedProtectedObjects);
    }

    public void updateProtectedObjects() {
        protectedObjectsData.processRequests(protectedObjectsData.getRequestList());
    }
}
