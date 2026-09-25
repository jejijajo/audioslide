package com.audioslide.video;

import com.audioslide.video.config.MinioConfig;
import com.audioslide.video.event.FileLinksEvent;
import com.audioslide.video.event.VideoCreatedEvent;
import com.audioslide.video.exception.StorageException;
import com.audioslide.video.exception.VideoProcessingException;
import com.audioslide.video.render.VideoRender;
import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;
    private final RabbitTemplate rabbitTemplate;
    private final VideoRender videoRender;

    public void createVideo(FileLinksEvent fileLinksEvent) {

        // GET FILES

        Path tempPicture = createFile("picture-", ".jpg", fileLinksEvent.pictureObjectKey());
        Path tempAudio = createFile("audio-", ".mp3", fileLinksEvent.audioObjectKey());
        Path tempVideo;
        try {
            tempVideo = Files.createTempFile("video-", ".mp4");
            log.info("created temp video file");
        } catch (IOException e) {
            cleanupQuietly(tempPicture, tempAudio);
            throw new VideoProcessingException("Failed to create temp file for video", e);
        }

        // MAKE VIDEO -> PUT IN S3 -> SEND MESSAGE IN RABBIT

        try (InputStream videoStream = videoRender.render(tempVideo, tempPicture, tempAudio)) {

            log.info("Video rendered");

            String videoName = java.util.UUID.randomUUID() + ".mp4";
            String videoObjectKey = sendToStorage(videoName, tempVideo, videoStream);
            rabbitTemplate.convertAndSend(new VideoCreatedEvent(videoObjectKey, fileLinksEvent.videoId()));

            log.info("Message sended to queue, message");

            deleteAudioFile(fileLinksEvent.audioObjectKey());
        } catch (IOException e) {
            throw new VideoProcessingException("Failed to close video stream", e);
        } finally {
            cleanupQuietly(tempPicture, tempAudio, tempVideo);
            log.info("Temp files deleted");
        }
    }

    private Path createFile(String prefix, String suffix, String objectKey) {

        Path tempFile;
        try {
            tempFile = Files.createTempFile(prefix, suffix);
        } catch (Exception e) {
            throw new VideoProcessingException("Failed to create temp file", e);
        }

        try (GetObjectResponse file = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBuckets().getFirst())
                        .object(objectKey)
                        .build())) {

            Files.copy(file, tempFile, StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            cleanupQuietly(tempFile);
            throw new VideoProcessingException(
                    "Failed to download/copy file, key: " + objectKey, e);
        }

        log.info("created temp file, object key: " + objectKey + ", prefix: " + prefix + ", suffix: " + suffix);

        return tempFile;
    }

    private String sendToStorage(String videoName, Path tempVideo, InputStream videoStream) {
        String objectKey;

        try{
            objectKey = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBuckets().get(1))
                            .object(videoName)
                            .stream(videoStream,Files.size(tempVideo),-1)
                            .contentType("video/mp4")
                            .build()
            ).object();

        } catch(Exception e) {
            throw new StorageException(
                    "Failed to upload video to MiniO, videoName: " + videoName +
                            ", bucket: " + minioConfig.getBuckets().get(1) ,e);
        }

        log.info("video sent to storage, key: " + objectKey);

        return objectKey;
    }

    private void deleteAudioFile(String S3Key) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBuckets().getFirst())
                            .object(S3Key)
                            .build()
            );
        } catch(Exception e) {
            throw new StorageException("Failed to delete audio file, key: " + S3Key +
                    ", bucket: " + minioConfig.getBuckets().getFirst(), e);
        }

        log.info("Audio deleted from s3, key: " + S3Key);
    }


    // DELETE TEMP FILES WITH LOGS
    private void cleanupQuietly(Path... paths) {
        for (Path path : paths) {
            try {
                Files.deleteIfExists(path);
            } catch (Exception e) {

            }
        }
    }
}
