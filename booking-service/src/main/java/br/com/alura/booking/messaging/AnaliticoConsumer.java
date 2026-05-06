package br.com.alura.booking.messaging;

import br.com.alura.booking.event.ReservaCriadaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class AnaliticoConsumer {

    private static final Logger log = LoggerFactory.getLogger(AnaliticoConsumer.class);

    private final Set<String> eventosProcessados =
            Collections.synchronizedSet(new HashSet<>());

    @KafkaListener(
        topics  = "${reserva.kafka.topic}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onReservaRegistrada(ReservaCriadaEvent event) {
        if (eventosProcessados.contains(event.getEventId())) {
            log.warn("[ANALYTICS] Evento duplicado ignorado: {}", event.getEventId());
            return;
        }
        eventosProcessados.add(event.getEventId());
        log.info("[ANALYTICS] ReservaRegistrada: reservaId={}, salaId={}, usuarioId={}",
                event.getReservaId(), event.getSalaId(), event.getUsuarioId());
    }
}
