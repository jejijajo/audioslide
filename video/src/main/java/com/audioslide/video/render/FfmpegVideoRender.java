package com.audioslide.video.render;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FfmpegVideoRender implements VideoRender {

    @Override
    public InputStream render(Path tempVideo, Path tempPicture, Path tempAudio) throws IOException {
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
}
