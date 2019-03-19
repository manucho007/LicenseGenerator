package ru.rtksoftlabs.licensegenerator.dao;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Profile("!inno")
public class ProtectedObjectsDataImpl extends ProtectedObjectsDataBase {
    @PostConstruct
    private void init() {
        processRequests(getRequestList());
    }
}
