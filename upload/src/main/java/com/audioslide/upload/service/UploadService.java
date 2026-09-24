package com.audioslide.upload.service;

import com.audioslide.upload.VideoRepository;
import com.audioslide.upload.dto.UploadRequestDto;
import com.audioslide.upload.dto.UploadResponseDto;
import com.audioslide.upload.entity.VideoEntity;
import com.audioslide.upload.entity.VideoStatus;
import com.audioslide.upload.event.FileLinksEvent;
import com.audioslide.upload.event.VideoCreatedEvent;
import com.audioslide.upload.event.VideoPublishedEvent;
import com.audioslide.upload.event.YoutubeDataCreatedEvent;
import com.audioslide.upload.exception.StorageException;
import com.audioslide.upload.exception.VideoNotFoundException;
import com.audioslide.upload.mapper.VideoMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UploadService {

    private final MinioClient minioClient;
    private final RabbitTemplate rabbitTemplate;
    private final VideoRepository videoRepository;

    @Value("${rabbit.exchange.name}")
    private String EXCHANGE_NAME;

    @Value("${rabbit.queues.upload.routing-key}")
    private String UPLOAD_ROUTING_KEY;

    @Value("${rabbit.queues.youtube.routing-key}")
    private String YOUTUBE_ROUTING_KEY;

    @Transactional
    public UploadResponseDto upload(
            MultipartFile picture,
            MultipartFile audio,
            UploadRequestDto metadata,
            Long userId
    ) {
        String pictureObjectKey = putToMinio("pictures/", picture);
        String audioObjectKey = putToMinio("audio/", audio);

        VideoEntity entity = VideoMapper.uploadRequestDtoToVideoEntity(metadata);
        entity.setUserId(userId);
        entity.setThumbnailS3Key(pictureObjectKey);

        VideoEntity saved = videoRepository.save(entity);

        UploadResponseDto response = new UploadResponseDto(pictureObjectKey,audioObjectKey,saved.getId(),saved.getVideoStatus());
        FileLinksEvent fileLinksEvent = new FileLinksEvent(pictureObjectKey,audioObjectKey,saved.getId());

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, UPLOAD_ROUTING_KEY, fileLinksEvent);

        return response;
    }

    private String putToMinio(String prefix, MultipartFile file) {
        String key = prefix + UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            return minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("raw-uploads")
                            .object(key)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            ).object();
        } catch (Exception e) {
            throw new StorageException("Failed to upload to MinIO: key=" + key, e);
        }
    }

    @Transactional
    public void sendDataToYoutubeService(VideoCreatedEvent videoCreatedEvent) {
        VideoEntity videoEntity = videoRepository.findById(videoCreatedEvent.videoId())
                .orElseThrow(() -> new VideoNotFoundException("Video not found, id: " + videoCreatedEvent.videoId()));

        videoEntity.setVideoStatus(VideoStatus.READY);
        videoEntity.setVideoS3Key(videoCreatedEvent.videoObjectKey());
        videoRepository.save(videoEntity);

        YoutubeDataCreatedEvent youtubeEvent = VideoMapper.videoEntityToYoutubeDataCreatedEvent(videoEntity);

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, YOUTUBE_ROUTING_KEY, youtubeEvent);
    }

    @Transactional
    public void markAsPublished(VideoPublishedEvent event) {
        VideoEntity videoEntity = videoRepository.findById(event.id())
                .orElseThrow(() -> new VideoNotFoundException("Video not found, id: " + event.id()));

        videoEntity.setYoutubeVideoLink(event.YoutubeVideoLink());
        videoEntity.setVideoStatus(VideoStatus.PUBLISHED);
        deleteVideoFromS3(videoEntity.getVideoS3Key());
        videoEntity.setVideoS3Key(null);
        videoRepository.save(videoEntity);
    }

    private void deleteVideoFromS3(String videoS3Key) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket("media-output")
                            .object(videoS3Key)
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Failed to delete video from MiniO: key=" + videoS3Key, e);
        }


    }

}
