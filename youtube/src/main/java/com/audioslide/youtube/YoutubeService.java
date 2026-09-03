package com.audioslide.youtube;


import com.audioslide.youtube.config.MinioConfig;
import com.audioslide.youtube.event.YoutubeDataCreatedEvent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class YoutubeService {

    private final YouTube youTube;
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public void uploadVideo(YoutubeDataCreatedEvent youtubeDataCreatedEvent) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
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

        minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBuckets().get(1))
                        .object(youtubeDataCreatedEvent.thumbnailS3Key())
                        .build()
        );
    }
}
