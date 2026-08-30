package com.audioslide.upload;

import com.audioslide.upload.dto.UploadRequestDto;
import com.audioslide.upload.dto.UploadResponseDto;
import com.audioslide.upload.entity.VideoEntity;
import com.audioslide.upload.entity.VideoStatus;
import com.audioslide.upload.event.FileLinksEvent;
import com.audioslide.upload.event.VideoCreatedEvent;
import com.audioslide.upload.event.YoutubeDataCreatedEvent;
import com.audioslide.upload.mapper.VideoMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

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
            UploadRequestDto metadata
    ) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String pictureObjectKey = minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket("raw-uploads")
                        .object("pictures/" + UUID.randomUUID() + "_" + picture.getOriginalFilename())
                        .stream(picture.getInputStream(),picture.getSize(),-1)
                        .contentType(picture.getContentType())
                        .build()
        ).object();

        String audioObjectKey = minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket("raw-uploads")
                        .object("audio/" + UUID.randomUUID() + "_" + audio.getOriginalFilename())
                        .stream(audio.getInputStream(),audio.getSize(),-1)
                        .contentType(audio.getContentType())
                        .build()
        ).object();

        VideoEntity entity = VideoMapper.uploadRequestDtoToVideoEntity(metadata);
        entity.setThumbnailS3Key(pictureObjectKey);

        VideoEntity saved = videoRepository.save(entity);

        UploadResponseDto response = new UploadResponseDto(pictureObjectKey,audioObjectKey,saved.getId(),saved.getVideoStatus());
        FileLinksEvent fileLinksEvent = new FileLinksEvent(pictureObjectKey,audioObjectKey,saved.getId());

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, UPLOAD_ROUTING_KEY, fileLinksEvent);

        return response;
    }

    @Transactional
    public void sendDataToYoutubeService(VideoCreatedEvent videoCreatedEvent) {
        VideoEntity videoEntity = videoRepository.getById(videoCreatedEvent.videoId());
        videoEntity.setVideoStatus(VideoStatus.READY);
        videoEntity.setVideoS3Key(videoCreatedEvent.videoObjectKey());
        videoRepository.save(videoEntity);

        YoutubeDataCreatedEvent youtubeEvent = VideoMapper.videoEntityToYoutubeDataCreatedEvent(videoEntity);

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, YOUTUBE_ROUTING_KEY, youtubeEvent);
    }

}
