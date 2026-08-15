package com.audioslide.upload.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQInit {

    private final RabbitAdmin rabbitAdmin;

    @PostConstruct
    public void init() {
        rabbitAdmin.initialize();
    }

}
