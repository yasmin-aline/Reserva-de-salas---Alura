package service;

import dto.ReservaDTO;
import exception.RegraDeNegocioException;
import model.Reserva;
import model.Sala;
import model.StatusReserva;
import model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.ReservaRepository;
import repository.SalaRepository;
import repository.UsuarioRepository;

import java.time.LocalDateTime;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final SalaRepository salaRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaService(ReservaRepository reservaRepository, SalaRepository salaRepository, UsuarioRepository usuarioRepository) {
        this.reservaRepository = reservaRepository;
        this.salaRepository = salaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Page<ReservaDTO> listar(Pageable paginacao) {
        return reservaRepository.findAll(paginacao).map(ReservaDTO::new);
    }
    
    public Page<ReservaDTO> listarPorSala(Long salaId, Pageable paginacao) {
        return reservaRepository.findBySalaId(salaId, paginacao).map(ReservaDTO::new);
    }

    public Page<ReservaDTO> buscarPorIntervalo(Long salaId, LocalDateTime inicio, LocalDateTime fim, Pageable paginacao) {
        return reservaRepository.buscarPorIntervalo(salaId, inicio, fim, StatusReserva.ATIVA, paginacao).map(ReservaDTO::new);
    }

    public ReservaDTO buscarPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Reserva com ID " + id + " não encontrada."));
        return new ReservaDTO(reserva);
    }

    // @Transactional garante que toda a operação de criação seja atômica
    // Ou seja: ou a reserva é validada (leitura de conflito) e salva no banco,
    // ou ocorre um rollback completo e o banco não é alterado (prevenindo concorrência)
    @Transactional
    public ReservaDTO criar(ReservaDTO dto) {
        Sala sala = salaRepository.findById(dto.getSalaId())
                .orElseThrow(() -> new RegraDeNegocioException("Sala não encontrada."));
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado."));

        Reserva reserva = agendarReserva(sala, usuario, dto.getInicio(), dto.getFim());
        return new ReservaDTO(reserva);
    }
    
    @Transactional
    public Reserva agendarReserva(Sala sala, Usuario usuario, LocalDateTime inicio, LocalDateTime fim) {
        Reserva novaReserva = new Reserva(sala, usuario, inicio, fim);

        // Usamos a verificação passando id nulo, significando que é uma criação
        boolean existeConflito = reservaRepository.existeSobreposicaoComOutra(sala, inicio, fim, StatusReserva.ATIVA, null);
        if (existeConflito) {
            throw new RegraDeNegocioException("Já existe uma reserva ativa para esta sala que entra em conflito com o horário solicitado.");
        }

        return reservaRepository.save(novaReserva);
    }

    @Transactional
    public ReservaDTO atualizar(Long id, ReservaDTO dto) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Reserva com ID " + id + " não encontrada."));

        Sala sala = reserva.getSala().getId().equals(dto.getSalaId()) 
                ? reserva.getSala() 
                : salaRepository.findById(dto.getSalaId()).orElseThrow(() -> new RegraDeNegocioException("Sala não encontrada."));

        // Cria uma validação temporal para checar regras de datas do início/fim
        Reserva validacaoTemporaria = new Reserva(sala, reserva.getUsuario(), dto.getInicio(), dto.getFim());

        // Verificamos o conflito de atualização considerando a modificação
        boolean existeConflito = reservaRepository.existeSobreposicaoComOutra(sala, dto.getInicio(), dto.getFim(), StatusReserva.ATIVA, reserva.getId());
        if (existeConflito) {
            throw new RegraDeNegocioException("O novo horário solicitado entra em conflito com outra reserva já existente.");
        }

        reserva.setSala(sala);
        reserva.setInicio(dto.getInicio());
        reserva.setFim(dto.getFim());

        return new ReservaDTO(reserva);
    }

    @Transactional
    public void cancelarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RegraDeNegocioException("Reserva com ID " + reservaId + " não foi encontrada."));
        reserva.cancelar();
    }
}
