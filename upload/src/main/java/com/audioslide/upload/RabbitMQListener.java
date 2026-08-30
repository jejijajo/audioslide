package com.audioslide.upload;


import com.audioslide.upload.event.VideoCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RabbitMQListener {

    private final ObjectMapper objectMapper;
    private final UploadService uploadService;


    @RabbitListener(queues = "${rabbit-listener.queue.name}",containerFactory = "rabbitListenerContainerFactory")
    public void receiveMessage(Message message) throws Exception {
        VideoCreatedEvent event = objectMapper.readValue(message.getBody(), VideoCreatedEvent.class);
        uploadService.sendDataToYoutubeService(event);
    }
}
