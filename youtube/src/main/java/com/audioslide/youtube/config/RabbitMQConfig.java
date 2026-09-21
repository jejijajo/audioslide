package com.audioslide.youtube.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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
    private String YOUTUBE_VIDEO_PUBLISHED_EXCHANGE;

    @Value("${rabbit.queues.published.name}")
    private String YOUTUBE_VIDEO_PUBLISHED_QUEUE_NAME;

    @Value("${rabbit.queues.published.routing-key}")
    private String YOUTUBE_VIDEO_PUBLISHED_ROUTING_KEY;

    @Bean
    public TopicExchange exchange() {return new TopicExchange(YOUTUBE_VIDEO_PUBLISHED_EXCHANGE,true,false);}

    @Bean
    public Queue publishedQueue() {return QueueBuilder.durable(YOUTUBE_VIDEO_PUBLISHED_QUEUE_NAME).build();}

    @Bean
    Binding publishedBinding(Queue publishedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(publishedQueue).to(exchange).with(YOUTUBE_VIDEO_PUBLISHED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(MessageConverter messageConverter, ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setExchange(YOUTUBE_VIDEO_PUBLISHED_EXCHANGE);
        template.setRoutingKey(YOUTUBE_VIDEO_PUBLISHED_ROUTING_KEY);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new SimpleMessageConverter());
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

}
