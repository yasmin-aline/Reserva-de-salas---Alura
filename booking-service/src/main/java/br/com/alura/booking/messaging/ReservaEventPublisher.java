package br.com.alura.booking.messaging;

import br.com.alura.booking.event.ReservaCriadaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReservaEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ReservaEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final KafkaTemplate<String, ReservaCriadaEvent> kafkaTemplate;

    @Value("${reserva.rabbitmq.exchange}")
    private String exchange;

    @Value("${reserva.rabbitmq.routingkey}")
    private String routingKey;

    @Value("${reserva.kafka.topic}")
    private String kafkaTopic;

    public ReservaEventPublisher(RabbitTemplate rabbitTemplate,
                                  KafkaTemplate<String, ReservaCriadaEvent> kafkaTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.kafkaTemplate  = kafkaTemplate;
    }

    public void publicarReservaCriada(ReservaCriadaEvent event) {
        log.info("[RabbitMQ] Publicando evento: {}", event);
        rabbitTemplate.convertAndSend(exchange, routingKey, event);

        log.info("[Kafka] Publicando evento: {}", event);
        kafkaTemplate.send(kafkaTopic, event.getEventId(), event);
    }
}