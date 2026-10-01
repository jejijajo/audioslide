package com.audioslide.video.render;

import com.audioslide.video.exception.VideoProcessingException;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Component
public class FfmpegVideoRender implements VideoRender {

    @Override
    public InputStream render(Path tempVideo, Path tempPicture, Path tempAudio) {
        FFmpeg ffmpeg = safeCall(() -> new FFmpeg("ffmpeg"), "Failed to create FFmpeg object");

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

        FFmpegExecutor executor = safeCall(() -> new FFmpegExecutor(ffmpeg),"Failed to create FFmpeg excecutor");

        try {
            executor.createJob(builder).run();
        } catch (Exception e) {
            throw new VideoProcessingException("Failed to create video", e);
        }

        InputStream inputStream = safeCall(() -> Files.newInputStream(tempVideo),"Failed to put file in input stream");

        return inputStream;
    }

    private <T> T safeCall(Callable<T> action, String errorMsg) {
        try {
            return action.call();
        } catch (Exception e) {
            throw new VideoProcessingException(errorMsg, e);
        }
    }
}
