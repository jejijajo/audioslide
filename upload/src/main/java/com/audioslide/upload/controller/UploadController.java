package com.audioslide.upload.controller;

import com.audioslide.upload.dto.UploadResponseDto;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    public final MinioClient minioClient;

    @PostMapping
    public UploadResponseDto upload(@RequestPart("picture") MultipartFile picture, @RequestPart("audio") MultipartFile audio) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        String pictureEtag = minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket("raw-uploads")
                        .object("pictures/" + picture.getOriginalFilename())
                        .stream(picture.getInputStream(),picture.getSize(),-1)
                        .contentType(picture.getContentType())
                        .build()
        ).etag();

        String audioEtag = minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket("raw-uploads")
                        .object("audio/" + audio.getOriginalFilename())
                        .stream(audio.getInputStream(),audio.getSize(),-1)
                        .contentType(audio.getContentType())
                        .build()
        ).etag();


        /*

        FFmpeg ffmpeg = new FFmpeg("FFmpeg");

        FFmpegBuilder builder = new FFmpegBuilder()
                .addExtraArgs("-loop", "1")
                .addInput(pictureFile.toString())
                .addInput(audioFile.toString());

        builder.setComplexFilter(
                "[0:v]scale=1920:1080:force_original_aspect_ratio=decrease,pad=1920:1080:(ow-iw)/2:(oh-ih)/2:black[v]"
        );

        builder.addOutput("D:/dv/temp/audioslide/video/"
                        + audio.getOriginalFilename().substring(0, audio.getOriginalFilename().length()-3) + "mp4")
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

        */

        return new UploadResponseDto(pictureEtag,audioEtag);
    }
}
