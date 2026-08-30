package com.audioslide.upload.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbit.exchange.name}")
    private String EXCHANGE_NAME;

    @Value("${rabbit.queues.upload.name}")
    private String UPLOAD_QUEUE_NAME;

    @Value("${rabbit.queues.upload.routing-key}")
    private String UPLOAD_ROUTING_KEY;

    @Value("${rabbit.queues.youtube.name}")
    private String YOUTUBE_QUEUE_NAME;

    @Value("${rabbit.queues.youtube.routing-key}")
    private String YOUTUBE_ROUTING_KEY;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue uploadQueue() {
        return QueueBuilder.durable(UPLOAD_QUEUE_NAME).build();
    }

    @Bean
    public Queue youtubeQueue() {
        return QueueBuilder.durable(YOUTUBE_QUEUE_NAME).build();
    }

    @Bean
    public Binding uploadBinding(Queue uploadQueue, TopicExchange exchange) {
        return BindingBuilder.bind(uploadQueue).to(exchange).with(UPLOAD_ROUTING_KEY);
    }

    @Bean
    public Binding youtubeBinding(Queue youtubeQueue, TopicExchange exchange) {
        return BindingBuilder.bind(youtubeQueue).to(exchange).with(YOUTUBE_ROUTING_KEY);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(MessageConverter messageConverter, ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new SimpleMessageConverter());
        return factory;
    }
}
