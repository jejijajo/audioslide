package com.audioslide.video;

import com.audioslide.video.config.MinioConfig;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
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

    public void createVideo(FileLinksEvent fileLinksEvent) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        // GET FILES

        GetObjectResponse audioFile = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBuckets().get(0))
                        .object(fileLinksEvent.audioObjectKey())
                        .build()
        );

        GetObjectResponse pictureFile = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBuckets().get(0))
                        .object(fileLinksEvent.pictureObjectKey())
                        .build()
        );

        Path tempPicture = Files.createTempFile("picture-", ".jpg");
        Path tempAudio = Files.createTempFile("audio-", ".mp3");
        Path tempVideo = Files.createTempFile("video-", ".mp4");

        Files.copy(pictureFile, tempPicture, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(audioFile, tempAudio, StandardCopyOption.REPLACE_EXISTING);

        String audioName = audioFile.object();
        String pictureName = pictureFile.object();
        String videoName = java.util.UUID.randomUUID() + ".mp4";

        // CREATE VIDEO

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

        InputStream videoStream = Files.newInputStream(tempVideo);

        // PUT VIDEO IN STORAGE

        String videoObjectKey = minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioConfig.getBuckets().get(1))
                        .object(videoName)
                        .stream(videoStream,Files.size(tempVideo),-1)
                        .contentType("video/mp4")
                        .build()
        ).object();

        Files.deleteIfExists(tempPicture);
        Files.deleteIfExists(tempAudio);
        Files.deleteIfExists(tempVideo);

    }

}
