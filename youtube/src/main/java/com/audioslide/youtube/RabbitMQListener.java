package com.audioslide.youtube;

import com.audioslide.youtube.event.VideoCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RabbitMQListener {

    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${rabbit-listener.queue.name}")
    public void receiveMessage(Message message) throws Exception {
        VideoCreatedEvent event = objectMapper.readValue(message.getBody(), VideoCreatedEvent.class);
        System.out.println(event.videoObjectKey());
    }
}
