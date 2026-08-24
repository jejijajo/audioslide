package com.audioslide.video;

import com.audioslide.video.config.MinioConfig;
import com.audioslide.video.event.FileLinksEvent;
import com.audioslide.video.event.VideoCreatedEvent;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
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

    public void createVideo(FileLinksEvent fileLinksEvent) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        // GET FILES

        Path tempPicture = createFile("picture-", ".jpg", fileLinksEvent.pictureObjectKey());
        Path tempAudio = createFile("audio-", ".mp3", fileLinksEvent.audioObjectKey());
        Path tempVideo = Files.createTempFile("video-", ".mp4");

        String videoName = java.util.UUID.randomUUID() + ".mp4";

        // CREATE VIDEO

        InputStream videoStream = videoFFmpeg(tempVideo,tempPicture,tempAudio);

        // PUT VIDEO IN STORAGE

        String videoObjectKey = sendToStorage(videoName,tempVideo,videoStream);

        // SEND MESSAGE TO QUEUE

        rabbitTemplate.convertAndSend(new VideoCreatedEvent(videoObjectKey));

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

    private InputStream videoFFmpeg(Path tempVideo, Path tempPicture, Path tempAudio) throws IOException {

        FFmpeg ffmpeg = new FFmpeg("ffmpeg");

        FFmpegBuilder builder = new FFmpegBuilder()
                .addExtraArgs("-loop", "1")
                .addInput(tempPicture.toString())
                .addInput(tempAudio.toString());

        builder.setComplexFilter(
                "[0:v]scale=1920:1080:force_original_aspect_ratio=decrease,pad=1920:1080:(ow-iw)/2:(oh-ih)/2:black[v]"
        );


        builder.addOutput(tempVideo.toString())
                .addExtraArgs("-shortest")
                .addExtraArgs("-map", "[v]")
                .addExtraArgs("-map", "1:a")
                .setVideoCodec("libx264")
                .setAudioCodec("aac")
                .setAudioBitRate(192000)
                .addExtraArgs("-pix_fmt", "yuv420p")
                .addExtraArgs("-tune", "stillimage")
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
        executor.createJob(builder).run();

        return Files.newInputStream(tempVideo);
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
