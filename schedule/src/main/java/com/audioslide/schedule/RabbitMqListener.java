package com.audioslide.schedule;

import com.audioslide.schedule.event.VideoPublishScheduledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RabbitMqListener {

    private final ScheduleService scheduleService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${rabbit-listener.queue.name}", containerFactory = "rabbitListenerContainerFactory")
    public void onVideoPosted(VideoPublishScheduledEvent event) {
        scheduleService.setLastUploadDateTime(event);
    }
}
