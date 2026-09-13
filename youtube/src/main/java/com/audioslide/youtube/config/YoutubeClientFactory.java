package com.audioslide.youtube.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import org.springframework.stereotype.Component;

@Component
public class YoutubeClientFactory {

    private static final String APPLICATION_NAME = "Audioslide";

    public YouTube build(String accessToken) throws Exception {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        HttpRequestInitializer requestInitializer = request ->
                request.getHeaders().setAuthorization("Bearer " + accessToken);

        return new YouTube.Builder(httpTransport, GsonFactory.getDefaultInstance(), requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
