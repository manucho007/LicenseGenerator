package ru.rtksoftlabs.licensegenerator.inno;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.rtksoftlabs.licensegenerator.ProtectedObject;
import ru.rtksoftlabs.licensegenerator.ProtectedObjectsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Profile("inno")
public class ProtectedObjectsServiceImpl implements ProtectedObjectsService {
    @Override
    public List<ProtectedObject> getProtectedObjects() {
        List<ProtectedObject> protectedObjects = new ArrayList<>();

        Map<String, String> components = new HashMap<>();

        components.put("idComponent1", "nameComponent1");
        components.put("idComponent2", "nameComponent2");
        components.put("idComponent3", "nameComponent3");

        protectedObjects.add(new ProtectedObject("App1", components));
        protectedObjects.add(new ProtectedObject("App2", components));

        return protectedObjects;
    }
}
