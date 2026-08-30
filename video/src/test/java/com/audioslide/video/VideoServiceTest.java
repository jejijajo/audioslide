package com.audioslide.video;

import com.audioslide.video.config.MinioConfig;
import com.audioslide.video.event.FileLinksEvent;
import com.audioslide.video.event.VideoCreatedEvent;
import com.audioslide.video.render.VideoRender;
import io.minio.*;
import io.minio.errors.*;
import okhttp3.Headers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VideoServiceTest {

    private VideoService videoService;

    @Mock
    private MinioClient minioClient;

    private MinioConfig minioConfig;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private VideoRender videoRender;

    @BeforeEach
    void setUp() {
        minioConfig = new MinioConfig();
        minioConfig.setBuckets(List.of("input-bucket", "output-bucket"));

        videoService = new VideoService(minioClient,minioConfig,rabbitTemplate,videoRender);
    }

    /*
    @Test
    public void shouldDownloadPictureAndAudioFromFirstBucket() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        InputStream fakeStream = new ByteArrayInputStream("12345".getBytes());
        FileLinksEvent fileLinksEvent = new FileLinksEvent("picture-key.jpg", "audio-key.mp3");

        GetObjectResponse fakeResponse = new GetObjectResponse(
                Headers.of(new HashMap<>()),
                "input-bucket",
                null,
                "object-key",
                fakeStream
        );

        when(minioClient.getObject(any())).thenReturn(fakeResponse);

        try { videoService.createVideo(fileLinksEvent); }
        catch (Exception e) { }

        ArgumentCaptor<GetObjectArgs> captor = ArgumentCaptor.forClass(GetObjectArgs.class);
        verify(minioClient, times(2)).getObject(captor.capture());

        List<GetObjectArgs> allCalls = captor.getAllValues();

        assertEquals("input-bucket", allCalls.get(0).bucket());
        assertEquals("input-bucket", allCalls.get(1).bucket());
    }

    @Test
    public void shouldPublishVideoCreatedEventWithKeyReturnedFromStorage() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        String videoName = "video.mp4";
        Path tempVideo = Path.of("D:/path/video.mp4");
        InputStream stream = new ByteArrayInputStream("12345".getBytes());
        FileLinksEvent fileLinksEvent = new FileLinksEvent("picture-key.jpg", "audio-key.mp3");

        ObjectWriteResponse fakeWriteResponse = new ObjectWriteResponse(
                Headers.of(new HashMap<>()),
                "output-bucket",
                null,
                "object-video-key",
                "etag",
                "version-1"
        );

        GetObjectResponse fakeGetResponse = new GetObjectResponse(
                Headers.of(new HashMap<>()),
                "input-bucket",
                null,
                "object-key",
                stream
        );

        when(minioClient.getObject(any())).thenReturn(fakeGetResponse);
        when(minioClient.putObject(any())).thenReturn(fakeWriteResponse);
        when(videoRender.render(any(),any(),any())).thenReturn(stream);

        try { videoService.createVideo(fileLinksEvent); }
        catch (Exception e) {}

        ArgumentCaptor<VideoCreatedEvent> captor = ArgumentCaptor.forClass(VideoCreatedEvent.class);
        verify(rabbitTemplate, times(1)).convertAndSend(captor.capture());



        assertEquals("object-video-key",captor.getValue().videoObjectKey());

    }

    @Test
    public void shouldDeleteAllTempFilesAfterSuccessfulRun() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        InputStream stream = new ByteArrayInputStream("12345".getBytes());
        FileLinksEvent fileLinksEvent = new FileLinksEvent("picture-key.jpg", "audio-key.mp3");
        ObjectWriteResponse fakeWriteResponse = new ObjectWriteResponse(
                Headers.of(new HashMap<>()),
                "output-bucket",
                null,
                "object-video-key",
                "etag",
                "version-1"
        );

        GetObjectResponse fakeGetResponse = new GetObjectResponse(
                Headers.of(new HashMap<>()),
                "input-bucket",
                null,
                "object-key",
                stream
        );

        when(minioClient.getObject(any())).thenReturn(fakeGetResponse);
        when(minioClient.putObject(any())).thenReturn(fakeWriteResponse);
        when(videoRender.render(any(),any(),any())).thenReturn(stream);

        try { videoService.createVideo(fileLinksEvent);} catch (Exception e) {
            throw new RuntimeException(e);
        }

        ArgumentCaptor<Path> argumentCaptor = ArgumentCaptor.forClass(Path.class);
        verify(videoRender, times(1)).render(argumentCaptor.capture(),argumentCaptor.capture(),argumentCaptor.capture());

        List<Path> allCalls = argumentCaptor.getAllValues();

        assertFalse(Files.exists(allCalls.get(0)));
        assertFalse(Files.exists(allCalls.get(1)));
        assertFalse(Files.exists(allCalls.get(2)));


    }
    */
}
