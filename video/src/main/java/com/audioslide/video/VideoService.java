package com.audioslide.video;

import com.audioslide.video.config.MinioConfig;
import com.audioslide.video.event.FileLinksEvent;
import com.audioslide.video.event.VideoCreatedEvent;
import com.audioslide.video.render.VideoRender;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;
    private final RabbitTemplate rabbitTemplate;
    private final VideoRender videoRender;

    public void createVideo(FileLinksEvent fileLinksEvent) throws Exception{

        // GET FILES

        Path tempPicture = createFile("picture-", ".jpg", fileLinksEvent.pictureObjectKey());
        Path tempAudio = createFile("audio-", ".mp3", fileLinksEvent.audioObjectKey());
        Path tempVideo = Files.createTempFile("video-", ".mp4");

        String videoName = java.util.UUID.randomUUID() + ".mp4";

        // CREATE VIDEO

        InputStream videoStream = videoRender.render(tempVideo,tempPicture,tempAudio);

        // PUT VIDEO IN STORAGE

        String videoObjectKey = sendToStorage(videoName,tempVideo,videoStream);

        // SEND MESSAGE TO QUEUE

        rabbitTemplate.convertAndSend(new VideoCreatedEvent(videoObjectKey,fileLinksEvent.videoId()));

        // DELETE TEMP FILES

        Files.deleteIfExists(tempPicture);
        Files.deleteIfExists(tempAudio);
        Files.deleteIfExists(tempVideo);

    }

    private Path createFile(String prefix, String suffix, String objectKey) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetObjectResponse file = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBuckets().getFirst())
                        .object(objectKey)
                        .build()
        );

        Path tempPicture = Files.createTempFile(prefix, suffix);

        Files.copy(file, tempPicture, StandardCopyOption.REPLACE_EXISTING);

        return tempPicture;
    }

    private String sendToStorage(String videoName, Path tempVideo, InputStream videoStream) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioConfig.getBuckets().get(1))
                        .object(videoName)
                        .stream(videoStream,Files.size(tempVideo),-1)
                        .contentType("video/mp4")
                        .build()
        ).object();
    }
}
