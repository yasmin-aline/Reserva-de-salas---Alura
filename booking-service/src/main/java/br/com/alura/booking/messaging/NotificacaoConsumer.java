package br.com.alura.booking.messaging;

import br.com.alura.booking.event.ReservaCriadaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class NotificacaoConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoConsumer.class);

    private final Set<String> eventosProcessados =
            Collections.synchronizedSet(new HashSet<>());

    @RabbitListener(queues = "${reserva.rabbitmq.queue}")
    public void onReservaCriada(ReservaCriadaEvent event) {
        if (eventosProcessados.contains(event.getEventId())) {
            log.warn("[NOTIFICACAO] Evento duplicado ignorado: {}", event.getEventId());
            return;
        }
        eventosProcessados.add(event.getEventId());
        log.info("[NOTIFICACAO] Reserva confirmada! reservaId={}, usuarioId={}, salaId={}, periodo={} ate {}",
                event.getReservaId(), event.getUsuarioId(), event.getSalaId(),
                event.getInicio(), event.getFim());
    }
}
