package ru.rtksoftlabs.licensegenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Profile("default")
public class ProtectedObjectsServiceImpl implements ProtectedObjectsService {
    @Autowired
    private ProtectedObjectsData protectedObjectsData;

    @Override
    public List<ProtectedObject> getProtectedObjects() {
        List<ProtectedObject> returnProtectedObjects = new ArrayList<>();

        for (List<ProtectedObject> protectedObjects: protectedObjectsData.getProtectedObjects().values()) {
            returnProtectedObjects.addAll(protectedObjects);
        }

        Collections.sort(returnProtectedObjects, Comparator.comparing(p -> p.data));

        return returnProtectedObjects;
    }

    public void updateProtectedObjects() {
        protectedObjectsData.processRequests(protectedObjectsData.getRequestList());
    }
}
