package com.audioslide.upload;

import com.audioslide.upload.dto.UploadResponseDto;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UploadServiceTests {

    @InjectMocks
    private UploadService uploadService;

    @Mock
    private MinioClient minioClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    /*

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(uploadService, "EXCHANGE_NAME", "test-exchange");
        ReflectionTestUtils.setField(uploadService, "ROUTING_KEY", "test-routing-key");
    }

    @Test
    public void upload_whenFilesAreValid_returnsUploadResponseDto() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MultipartFile picture = new MockMultipartFile("picture", "pic.jpg", "image/jpeg", "data".getBytes());
        MultipartFile audio = new MockMultipartFile("audio", "audio.mp3", "audio/mpeg", "data".getBytes());

        ObjectWriteResponse pictureResponse = mock(ObjectWriteResponse.class);
        when(pictureResponse.object()).thenReturn("pictures/uuid_pic.jpg");

        ObjectWriteResponse audioResponse = mock(ObjectWriteResponse.class);
        when(audioResponse.object()).thenReturn("audio/uuid_audio.mp3");

        when(minioClient.putObject(any()))
                .thenReturn(pictureResponse)
                .thenReturn(audioResponse);

        UploadResponseDto result = uploadService.upload(picture, audio);

        assertEquals(result.pictureObjectKey(),"pictures/uuid_pic.jpg");
        assertEquals(result.audioObjectKey(),"audio/uuid_audio.mp3");

        verify(rabbitTemplate).convertAndSend(anyString(),anyString(),eq(result));
    }

    @Test
    public void upload_whenPictureUploadFails_propagatesExceptionAndSkipsAudio() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MultipartFile picture = new MockMultipartFile("picture", "pic.jpg", "image/jpeg", "data".getBytes());
        MultipartFile audio = new MockMultipartFile("audio", "audio.mp3", "audio/mpeg", "data".getBytes());

        when(minioClient.putObject(any()))
                .thenThrow(new IOException("upload failed"));

        assertThrows(IOException.class, () -> uploadService.upload(picture, audio));

        verify(minioClient, times(1)).putObject(any());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));
    }

    @Test
    public void upload_whenCalled_sendsCorrectBucketAndKeysToMinio() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MultipartFile picture = new MockMultipartFile("picture", "pic.jpg", "image/jpeg", "data".getBytes());
        MultipartFile audio = new MockMultipartFile("audio", "audio.mp3", "audio/mpeg", "data".getBytes());

        ObjectWriteResponse pictureResponse = mock(ObjectWriteResponse.class);
        when(pictureResponse.object()).thenReturn("pictures/uuid_pic.jpg");

        ObjectWriteResponse audioResponse = mock(ObjectWriteResponse.class);
        when(audioResponse.object()).thenReturn("audio/uuid_audio.mp3");

        when(minioClient.putObject(any()))
                .thenReturn(pictureResponse)
                .thenReturn(audioResponse);

        uploadService.upload(picture, audio);

        ArgumentCaptor<PutObjectArgs> captor = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(minioClient, times(2)).putObject(captor.capture());

        PutObjectArgs pictureArgs = captor.getAllValues().get(0);
        PutObjectArgs audioArgs = captor.getAllValues().get(1);

        assertEquals("raw-uploads", pictureArgs.bucket());
        assertTrue(pictureArgs.object().startsWith("pictures/"));
        assertTrue(pictureArgs.object().endsWith("pic.jpg"));

        assertEquals("raw-uploads", audioArgs.bucket());
        assertTrue(audioArgs.object().startsWith("audio/"));
        assertTrue(audioArgs.object().endsWith("audio.mp3"));
    }

    */
}
