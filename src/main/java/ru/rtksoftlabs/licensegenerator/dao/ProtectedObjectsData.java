package ru.rtksoftlabs.licensegenerator.dao;

import reactor.core.publisher.Mono;
import ru.rtksoftlabs.LicenseCommons.shared.ProtectedObject;
import ru.rtksoftlabs.LicenseCommons.shared.ProtectedObjects;

import java.util.List;
import java.util.Map;

public interface ProtectedObjectsData {
    Map<String, ProtectedObjects> getProtectedObjects();
    Map<String, Mono<ProtectedObjects>> getRequestList();
    Mono<List<ProtectedObject>> getRequest(String server);
    void addToMap(String key, ProtectedObjects protectedObjects);
    void processRequests(Map<String, Mono<ProtectedObjects>> requests);
}
