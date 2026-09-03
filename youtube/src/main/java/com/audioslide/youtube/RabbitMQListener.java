package com.audioslide.youtube;

import com.audioslide.youtube.event.YoutubeDataCreatedEvent;
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
        YoutubeDataCreatedEvent event = objectMapper.readValue(message.getBody(), YoutubeDataCreatedEvent.class);
        System.out.println(event);
    }
}
