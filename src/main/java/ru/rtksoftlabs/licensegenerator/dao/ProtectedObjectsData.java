package ru.rtksoftlabs.licensegenerator.dao;

import reactor.core.publisher.Mono;
import ru.rtksoftlabs.LicenseCommons.shared.ProtectedObject;

import java.util.List;
import java.util.Map;

public interface ProtectedObjectsData {
    Map<String, List<ProtectedObject>> getProtectedObjects();
    Map<String, Mono<List<ProtectedObject>>> getRequestList();
    Mono<List<ProtectedObject>> getRequest(String server);
    void addToMap(String key, List<ProtectedObject> list);
    void processRequests(Map<String, Mono<List<ProtectedObject>>> requests);
}
