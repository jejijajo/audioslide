package com.audioslide.upload;

import com.audioslide.upload.dto.UploadResponseDto;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

    @Value("${rabbit.exchange.name}")
    private String EXCHANGE_NAME;

    @Value("${rabbit.routing-key.name}")
    private String ROUTING_KEY;

    public UploadResponseDto upload(MultipartFile picture, MultipartFile audio) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
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

        UploadResponseDto response = new UploadResponseDto(pictureObjectKey,audioObjectKey);

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, response);

        return response;
    }

}
