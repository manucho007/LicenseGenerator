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
        List<ProtectedObject> protectedObjects = new ArrayList<>();

        ProtectedObject protectedObject1 = new ProtectedObject("App1");
        ProtectedObject protectedObject2 = new ProtectedObject("App2");

        ProtectedObject child = protectedObject1.addChild("Scripts");

        child.addChild("sc1");
        child.addChild("sc2");
        child.addChild("sc3");

        protectedObject1.addChild("Roles");

        protectedObjects.add(protectedObject1);
        protectedObjects.add(protectedObject2);

        return protectedObjects;
    }

    @Override
    public void updateProtectedObjects() {

    }
}
