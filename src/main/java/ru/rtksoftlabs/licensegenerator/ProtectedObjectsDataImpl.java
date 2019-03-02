package ru.rtksoftlabs.licensegenerator;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Profile("default")
public class ProtectedObjectsDataImpl extends ProtectedObjectsDataBase {
    @PostConstruct
    private void init() {
        processRequests(getRequestList());
    }
}
