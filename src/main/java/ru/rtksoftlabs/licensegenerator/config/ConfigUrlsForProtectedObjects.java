package ru.rtksoftlabs.licensegenerator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties("")
public class ConfigUrlsForProtectedObjects {
    private final Map<String, String> servers = new HashMap<>();

    public Map<String, String> getServers() {
        return servers;
    }
}
