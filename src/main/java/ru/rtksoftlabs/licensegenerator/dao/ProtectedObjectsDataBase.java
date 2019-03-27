package ru.rtksoftlabs.licensegenerator.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.rtksoftlabs.LicenseCommons.shared.ProtectedObject;
import ru.rtksoftlabs.licensegenerator.config.ConfigUrlsForProtectedObjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class ProtectedObjectsDataBase implements ProtectedObjectsData {
    @Autowired
    private WebClient webClient;

    @Autowired
    private ConfigUrlsForProtectedObjects serverUrls;

    private final ConcurrentMap<String, List<ProtectedObject>> protectedObjects;

    public ProtectedObjectsDataBase() {
        protectedObjects = new ConcurrentHashMap<>();
    }

    public void addToMap(String key, List<ProtectedObject> list) {
        if((!protectedObjects.containsKey(key)) || (!protectedObjects.get(key).equals(list))) {
            protectedObjects.put(key, list);
        }
    }

    public void processRequests(Map<String, Mono<List<ProtectedObject>>> requests) {
        for (Map.Entry<String, Mono<List<ProtectedObject>>> request: requests.entrySet()) {
            request.getValue().subscribe(p -> addToMap(request.getKey(), p),
                    e -> {
                        protectedObjects.remove(request.getKey());

                        log.error("Request to " + request.getKey() + ": " + request.getValue() + " failed", e);
                    });
        }
    }

    @Override
    public Mono<List<ProtectedObject>> getRequest(String server) {
        return webClient.get().uri(server).retrieve().bodyToMono(new ParameterizedTypeReference<List<ProtectedObject>>() {});
    }

    @Override
    public Map<String, Mono<List<ProtectedObject>>> getRequestList() {
        Map<String, String> servers = serverUrls.getServers();

        Map<String, Mono<List<ProtectedObject>>> monosList = new HashMap<>();

        for (Map.Entry<String, String> entry: servers.entrySet()) {
            Mono<List<ProtectedObject>> protectedObjectsMono = webClient.get().uri(entry.getValue()).retrieve().bodyToMono(new ParameterizedTypeReference<List<ProtectedObject>>() {});

            monosList.put(entry.getKey(), protectedObjectsMono);
        }

        return monosList;
    }

    public Map<String, List<ProtectedObject>> getProtectedObjects() {
        return protectedObjects;
    }
}
