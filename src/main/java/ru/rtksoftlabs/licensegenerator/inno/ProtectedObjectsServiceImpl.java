package ru.rtksoftlabs.licensegenerator.inno;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.rtksoftlabs.licensegenerator.ProtectedObject;
import ru.rtksoftlabs.licensegenerator.ProtectedObjectsService;

import java.util.ArrayList;
import java.util.List;

@Service
@Profile("inno")
public class ProtectedObjectsServiceImpl implements ProtectedObjectsService {
    @Override
    public List<ProtectedObject> getProtectedObjects() {
        ArrayList<ProtectedObject> protectedObjects = new ArrayList<>();

        ArrayList<String> components = new ArrayList<>();
        components.add("Component1");
        components.add("Component2");
        components.add("Component3");

        protectedObjects.add(new ProtectedObject("App1", components));
        protectedObjects.add(new ProtectedObject("App2", components));

        return protectedObjects;
    }
}
