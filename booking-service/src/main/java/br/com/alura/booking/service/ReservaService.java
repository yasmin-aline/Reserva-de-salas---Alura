package br.com.alura.booking.service;

import br.com.alura.booking.dto.ReservaDTO;
import br.com.alura.booking.event.ReservaCriadaEvent;
import br.com.alura.booking.exception.RegraDeNegocioException;
import br.com.alura.booking.messaging.ReservaEventPublisher;
import br.com.alura.booking.model.Reserva;
import br.com.alura.booking.model.StatusReserva;
import br.com.alura.booking.repository.ReservaRepository;
import br.com.alura.booking.client.RoomClient;
import br.com.alura.booking.client.UserClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final RoomClient roomClient;
    private final UserClient userClient;
    private final ReservaEventPublisher eventPublisher;

    public ReservaService(ReservaRepository reservaRepository, RoomClient roomClient,
                          UserClient userClient, ReservaEventPublisher eventPublisher) {
        this.reservaRepository = reservaRepository;
        this.roomClient        = roomClient;
        this.userClient        = userClient;
        this.eventPublisher    = eventPublisher;
    }

    public Page<ReservaDTO> listar(Pageable paginacao) {
        return reservaRepository.findAll(paginacao).map(ReservaDTO::new);
    }

    public Page<ReservaDTO> listarPorSala(Long salaId, Pageable paginacao) {
        return reservaRepository.findBySalaId(salaId, paginacao).map(ReservaDTO::new);
    }

    public Page<ReservaDTO> buscarPorIntervalo(Long salaId, LocalDateTime inicio,
                                                LocalDateTime fim, Pageable paginacao) {
        return reservaRepository
                .buscarPorIntervalo(salaId, inicio, fim, StatusReserva.ATIVA, paginacao)
                .map(ReservaDTO::new);
    }

    public ReservaDTO buscarPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Reserva com ID " + id + " não encontrada."));
        return new ReservaDTO(reserva);
    }

    @Transactional
    public ReservaDTO criar(ReservaDTO dto) {
        roomClient.checkRoom(dto.getSalaId());
        userClient.checkUser(dto.getUsuarioId());

        Reserva reserva = agendarReserva(dto.getSalaId(), dto.getUsuarioId(),
                                          dto.getInicio(), dto.getFim());

        ReservaCriadaEvent event = new ReservaCriadaEvent(
                reserva.getId(), reserva.getSalaId(), reserva.getUsuarioId(),
                reserva.getInicio(), reserva.getFim());
        eventPublisher.publicarReservaCriada(event);

        return new ReservaDTO(reserva);
    }

    @Transactional
    public Reserva agendarReserva(Long salaId, Long usuarioId,
                                   LocalDateTime inicio, LocalDateTime fim) {
        Reserva novaReserva = new Reserva(salaId, usuarioId, inicio, fim);

        boolean existeConflito = reservaRepository.existeSobreposicaoComOutra(
                salaId, inicio, fim, StatusReserva.ATIVA, null);
        if (existeConflito) {
            throw new RegraDeNegocioException(
                    "Já existe uma reserva ativa para esta sala que entra em conflito com o horário solicitado.");
        }

        return reservaRepository.save(novaReserva);
    }

    @Transactional
    public ReservaDTO atualizar(Long id, ReservaDTO dto) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Reserva com ID " + id + " não encontrada."));

        if (!reserva.getSalaId().equals(dto.getSalaId())) {
            roomClient.checkRoom(dto.getSalaId());
        }

        boolean existeConflito = reservaRepository.existeSobreposicaoComOutra(
                dto.getSalaId(), dto.getInicio(), dto.getFim(),
                StatusReserva.ATIVA, reserva.getId());
        if (existeConflito) {
            throw new RegraDeNegocioException(
                    "O novo horário solicitado entra em conflito com outra reserva já existente.");
        }

        reserva.setSalaId(dto.getSalaId());
        reserva.setInicio(dto.getInicio());
        reserva.setFim(dto.getFim());

        return new ReservaDTO(reserva);
    }

    @Transactional
    public void cancelarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Reserva com ID " + reservaId + " não foi encontrada."));
        reserva.cancelar();
    }
}