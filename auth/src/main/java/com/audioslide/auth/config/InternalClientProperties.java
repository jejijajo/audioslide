package com.audioslide.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "internal")
@Getter
@Setter
public class InternalClientProperties {

    private Map<String, ClientConfig> clients;

    @Getter @Setter
    public static class ClientConfig {
        private String secret;
        private String scopes;
    }
}
