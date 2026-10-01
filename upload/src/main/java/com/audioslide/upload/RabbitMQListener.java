package com.audioslide.upload;


import com.audioslide.upload.event.VideoCreatedEvent;
import com.audioslide.upload.event.VideoPublishedEvent;
import com.audioslide.upload.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RabbitMQListener {

    private final ObjectMapper objectMapper;
    private final UploadService uploadService;


    @RabbitListener(queues = "${rabbit-listener.video-queue.name}",
            containerFactory = "rabbitListenerContainerFactory")
    public void onVideoCreated(Message message) {
        uploadService.sendDataToYoutubeService(parse(message, VideoCreatedEvent.class));
    }

    @RabbitListener(queues = "${rabbit-listener.youtube-published-queue.name}",
            containerFactory = "rabbitListenerContainerFactory")
    public void onVideoPublished(Message message) {
        uploadService.markAsPublished(parse(message, VideoPublishedEvent.class));
    }

    private <T> T parse(Message message, Class<T> type) {
        try {
            return objectMapper.readValue(message.getBody(), type);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Invalid message payload", e);
        }
    }
}
