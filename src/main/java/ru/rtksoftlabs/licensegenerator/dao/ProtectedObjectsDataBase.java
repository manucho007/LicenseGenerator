package ru.rtksoftlabs.licensegenerator.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.rtksoftlabs.LicenseCommons.services.JsonMapperService;
import ru.rtksoftlabs.LicenseCommons.shared.ProtectedObject;
import ru.rtksoftlabs.LicenseCommons.shared.ProtectedObjects;
import ru.rtksoftlabs.licensegenerator.config.ConfigUrlsForProtectedObjects;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class ProtectedObjectsDataBase implements ProtectedObjectsData {
    @Value("${webclient.read.mono.timeout}")
    private int readMonoTimeout;

    @Autowired
    private WebClient webClient;

    @Autowired
    private ConfigUrlsForProtectedObjects serverUrls;

    @Autowired
    private JsonMapperService jsonMapperService;

    private final ConcurrentMap<String, ProtectedObjects> protectedObjects;

    public ProtectedObjectsDataBase() {
        protectedObjects = new ConcurrentHashMap<>();
    }

    public void addToMap(String key, ProtectedObjects protectedObjects) {
        if((!this.protectedObjects.containsKey(key)) || (!this.protectedObjects.get(key).equals(protectedObjects))) {
            this.protectedObjects.put(key, protectedObjects);
        }
    }

    public void processRequests(Map<String, Mono<ProtectedObjects>> requests) {
        for (Map.Entry<String, Mono<ProtectedObjects>> request: requests.entrySet()) {
            request.getValue()
                    .timeout(Duration.ofMillis(readMonoTimeout))
                    .subscribe(p -> {
                        try {
                            addToMap(request.getKey(), p);
                        }
                        catch (RuntimeException e) {
                            try {
                                log.error("Error during addToMap, source: " + request.getKey() + ", response data in json: " + jsonMapperService.generateJson(p), e);
                            } catch (JsonProcessingException ex) {
                                log.error("Json processing error", ex);
                            }
                        }
                    },
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
    public Map<String, Mono<ProtectedObjects>> getRequestList() {
        Map<String, String> servers = serverUrls.getServers();

        Map<String, Mono<ProtectedObjects>> monosList = new HashMap<>();

        for (Map.Entry<String, String> entry: servers.entrySet()) {
            Mono<ProtectedObjects> protectedObjectsMono = webClient.get().uri(entry.getValue()).retrieve().bodyToMono(ProtectedObjects.class);

            monosList.put(entry.getKey(), protectedObjectsMono);
        }

        return monosList;
    }

    public Map<String, ProtectedObjects> getProtectedObjects() {
        return protectedObjects;
    }
}
