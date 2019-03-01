package ru.rtksoftlabs.licensegenerator;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ProtectedObjectsData {
    void requestProtectedObjects();
    Map<String, List<ProtectedObject>> getProtectedObjects();
    Map<String, Mono<List<ProtectedObject>>> getRequestList();
    Mono<List<ProtectedObject>> getRequest(String server);
    void addToMap(String key, List<ProtectedObject> list);
    void setProtectedObjects(Map<String, List<ProtectedObject>> protectedObjects);
}
