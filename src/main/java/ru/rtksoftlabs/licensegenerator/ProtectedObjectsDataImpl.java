package ru.rtksoftlabs.licensegenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.rtksoftlabs.licensegenerator.config.ConfigUrlsForProtectedObjects;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProtectedObjectsDataImpl implements ProtectedObjectsData {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient webClient;

    @Autowired
    private ConfigUrlsForProtectedObjects serverUrls;

    private Map<String, List<ProtectedObject>> protectedObjects;

    public ProtectedObjectsDataImpl() {
        protectedObjects = new ConcurrentHashMap<>();
    }

    public void addToMap(String key, List<ProtectedObject> list) {
        if((!protectedObjects.containsKey(key)) || (!protectedObjects.get(key).equals(list))) {
            protectedObjects.put(key, list);
        }
    }

    public void processRequests(Map<String, Mono<List<ProtectedObject>>> requests) {
        for (Map.Entry<String, Mono<List<ProtectedObject>>> request: requests.entrySet()) {
            request.getValue().subscribe(p -> {
                addToMap(request.getKey(), p);
            },
            e -> {
                throw new RuntimeException(e);
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

    @Override
    public void requestProtectedObjects() {

    }

    public Map<String, List<ProtectedObject>> getProtectedObjects() {
        return protectedObjects;
    }

    @Override
    public void setProtectedObjects(Map<String, List<ProtectedObject>> protectedObjects) {
        this.protectedObjects = protectedObjects;
    }
}
