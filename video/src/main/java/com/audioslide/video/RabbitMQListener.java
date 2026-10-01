package com.audioslide.video;

import com.audioslide.video.event.FileLinksEvent;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


@Component
@RequiredArgsConstructor
public class RabbitMQListener {

    private final VideoService videoService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${rabbit-listener.queue.name}", containerFactory = "rabbitListenerContainerFactory")
    public void receiveMessage(Message message) {
        FileLinksEvent event = objectMapper.readValue(message.getBody(), FileLinksEvent.class);

        videoService.createVideo(event);
    }
}
