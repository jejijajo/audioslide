package com.audioslide.youtube;


import com.audioslide.youtube.config.MinioConfig;
import com.audioslide.youtube.config.YoutubeClientFactory;
import com.audioslide.youtube.dto.YoutubeTokenResponseDto;
import com.audioslide.youtube.event.VideoPublishedEvent;
import com.audioslide.youtube.event.YoutubeDataCreatedEvent;
import com.audioslide.youtube.exception.StorageException;
import com.audioslide.youtube.exception.YoutubeAuthException;
import com.audioslide.youtube.exception.YoutubeProcessingException;
import com.audioslide.youtube.exception.YoutubeUploadException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.util.DateTime;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    private final RabbitTemplate rabbitTemplate;

    private final AuthServiceClient authServiceClient;

    private final YoutubeClientFactory youtubeClientFactory;


    public void uploadVideo(YoutubeDataCreatedEvent youtubeDataCreatedEvent) {
        Video video = new Video();

        //metadata

        VideoSnippet videoSnippet = new VideoSnippet();
        videoSnippet.setTitle(youtubeDataCreatedEvent.title());
        videoSnippet.setDescription(youtubeDataCreatedEvent.description());
        videoSnippet.setTags(youtubeDataCreatedEvent.tags());
        videoSnippet.setCategoryId(String.valueOf(youtubeDataCreatedEvent.categoryId()));



        VideoStatus videoStatus = new VideoStatus();
        videoStatus.setPrivacyStatus(youtubeDataCreatedEvent.privacyStatus().getValue());
        if (youtubeDataCreatedEvent.publishAt() != null) {
            videoStatus.setPublishAt(new DateTime(youtubeDataCreatedEvent.publishAt().toEpochMilli()));
        }


        video.setSnippet(videoSnippet);
        video.setStatus(videoStatus);

        //download pic+vid from s3

        byte[] thumbnailBytes;
        try (GetObjectResponse thumbnailStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBuckets().getFirst())
                        .object(youtubeDataCreatedEvent.thumbnailS3Key())
                        .build())) {

            thumbnailBytes = thumbnailStream.readAllBytes();
        } catch (Exception e) {
            throw new StorageException("Failed to download file from S3, key: "
                    + youtubeDataCreatedEvent.thumbnailS3Key()
                    + ", bucket: " + minioConfig.getBuckets().getFirst(),e);
        }

        log.info("Thumbnail downloaded from s3, key: " + youtubeDataCreatedEvent.thumbnailS3Key() + ", bucket: " + minioConfig.getBuckets().getFirst());

        byte[] videoBytes;
        try (GetObjectResponse videoStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBuckets().get(1))
                        .object(youtubeDataCreatedEvent.videoS3Key())
                        .build())) {

            videoBytes = videoStream.readAllBytes();
        } catch (Exception e) {
            throw new StorageException("Failed to download file from S3, key: "
                    + youtubeDataCreatedEvent.thumbnailS3Key()
                    + ", bucket: " + minioConfig.getBuckets().getFirst(),e);
        }

        log.info("Video downloaded from s3, key: " + youtubeDataCreatedEvent.thumbnailS3Key() + ", bucket: " + minioConfig.getBuckets().getFirst());

        // upload to channel

        YoutubeTokenResponseDto response = authServiceClient.getYoutubeToken(youtubeDataCreatedEvent.userId());

        YouTube youtube;
        try {
            youtube = youtubeClientFactory.build(response.accessToken());
        } catch (Exception e) {
            throw new YoutubeAuthException("Failed to create youtube client for user "
                    + youtubeDataCreatedEvent.userId(), e);
        }

        InputStreamContent mediaContent = new InputStreamContent(
                "video/*", new ByteArrayInputStream(videoBytes));
        mediaContent.setLength(videoBytes.length);


        Video uploadedVideo;
        try {
            YouTube.Videos.Insert videoInsert = youtube
                    .videos()
                    .insert(List.of("snippet", "status"), video, mediaContent);
            videoInsert.setNotifySubscribers(false);
            uploadedVideo = videoInsert.execute();
        } catch (GoogleJsonResponseException e) {
            throw new YoutubeUploadException("YouTube API rejected upload: "
                    + e.getDetails().getMessage(), e);
        } catch (IOException e) {
            throw new YoutubeUploadException("IO error during upload", e);
        }

        String videoId = uploadedVideo.getId();

        // send success message to upload service

        String videoUrl = "https://youtu.be/" + videoId;

        rabbitTemplate.convertAndSend(
                new VideoPublishedEvent(youtubeDataCreatedEvent.id(),videoUrl));

        try {
            InputStreamContent thumbnailContent = new InputStreamContent(
                    "image/jpeg", new ByteArrayInputStream(thumbnailBytes));
            thumbnailContent.setLength(thumbnailBytes.length);
            youtube.thumbnails().set(videoId, thumbnailContent).execute();
        } catch (Exception e) {
            log.warn("Failed to set thumbnail for {}", videoId, e);
        }

    }

}
