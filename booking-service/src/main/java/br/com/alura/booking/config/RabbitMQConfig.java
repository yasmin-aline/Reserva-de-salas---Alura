package br.com.alura.booking.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${reserva.rabbitmq.exchange}")
    private String exchange;

    @Value("${reserva.rabbitmq.queue}")
    private String queue;

    @Value("${reserva.rabbitmq.routingkey}")
    private String routingKey;

    @Bean
    public TopicExchange reservaExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue reservaCriadaQueue() {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    public Binding binding(Queue reservaCriadaQueue, TopicExchange reservaExchange) {
        return BindingBuilder.bind(reservaCriadaQueue)
                .to(reservaExchange)
                .with(routingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
