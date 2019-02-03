package ru.rtksoftlabs.licensegenerator;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("default")
public class ProtectedObjectsServiceImpl implements ProtectedObjectsService {
    @Override
    public List<ProtectedObject> getProtectedObjects() {
        return null;
    }
}
