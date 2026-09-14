package com.audioslide.youtube;


import com.audioslide.youtube.config.MinioConfig;
import com.audioslide.youtube.config.YoutubeClientFactory;
import com.audioslide.youtube.dto.YoutubeTokenResponseDto;
import com.audioslide.youtube.event.YoutubeDataCreatedEvent;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    private final AuthServiceClient authServiceClient;

    private final YoutubeClientFactory youtubeClientFactory;


    public void uploadVideo(YoutubeDataCreatedEvent youtubeDataCreatedEvent) throws Exception {
        Video video = new Video();

        //metadata

        VideoSnippet videoSnippet = new VideoSnippet();
        videoSnippet.setTitle(youtubeDataCreatedEvent.title());
        videoSnippet.setDescription(youtubeDataCreatedEvent.description());
        videoSnippet.setTags(youtubeDataCreatedEvent.tags());
        videoSnippet.setCategoryId(String.valueOf(youtubeDataCreatedEvent.categoryId()));

        VideoStatus videoStatus = new VideoStatus();
        videoStatus.setPrivacyStatus(youtubeDataCreatedEvent.privacyStatus().getValue());

        video.setSnippet(videoSnippet);
        video.setStatus(videoStatus);

        //download pic+vid from s3

        byte[] thumbnailBytes;
        try (GetObjectResponse thumbnailStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBuckets().get(0))
                        .object(youtubeDataCreatedEvent.thumbnailS3Key())
                        .build())) {

            thumbnailBytes = thumbnailStream.readAllBytes();
        }

        byte[] videoBytes;
        try (GetObjectResponse videoStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBuckets().get(1))
                        .object(youtubeDataCreatedEvent.videoS3Key())
                        .build())) {

            videoBytes = videoStream.readAllBytes();
        }

        // upload to channel

        YoutubeTokenResponseDto response = authServiceClient.getYoutubeToken(youtubeDataCreatedEvent.userId());

        YouTube youtube = youtubeClientFactory.build(response.accessToken());

        InputStreamContent mediaContent = new InputStreamContent(
                "video/*", new ByteArrayInputStream(videoBytes));
        mediaContent.setLength(videoBytes.length);

        YouTube.Videos.Insert videoInsert = youtube
                .videos()
                .insert(List.of("snippet", "status"), video, mediaContent);
        videoInsert.setNotifySubscribers(false);

        Video uploadedVideo = videoInsert.execute();
        String videoId = uploadedVideo.getId();

        InputStreamContent thumbnailContent = new InputStreamContent(
                "image/jpeg", new ByteArrayInputStream(thumbnailBytes));
        thumbnailContent.setLength(thumbnailBytes.length);

        youtube.thumbnails()
                .set(videoId, thumbnailContent)
                .execute();
    }

}
