package service;

import dto.ReservaDTO;
import exception.RegraDeNegocioException;
import model.Reserva;
import model.Sala;
import model.StatusReserva;
import model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.ReservaRepository;
import repository.SalaRepository;
import repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private SalaRepository salaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Sala sala;
    private Usuario usuario;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private ReservaDTO reservaDTO;

    @BeforeEach
    void setUp() {
        sala = new Sala(1L, "Sala de Reunião", 10, true);
        usuario = new Usuario(1L, "Maria", "maria@email.com");
        inicio = LocalDateTime.of(2025, 10, 10, 14, 0); // 14h
        fim = LocalDateTime.of(2025, 10, 10, 16, 0); // 16h
        
        reservaDTO = new ReservaDTO();
        reservaDTO.setSalaId(1L);
        reservaDTO.setUsuarioId(1L);
        reservaDTO.setInicio(inicio);
        reservaDTO.setFim(fim);
    }

    @Test
    @DisplayName("Deve criar reserva com sucesso quando nao ha conflito")
    void deveCriarReservaSemConflito() {
        // Arrange
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        
        // Simula que NÃO há sobreposição no banco
        when(reservaRepository.existeSobreposicaoComOutra(sala, inicio, fim, StatusReserva.ATIVA, null))
                .thenReturn(false);
        
        Reserva reservaSalva = new Reserva(sala, usuario, inicio, fim);
        reservaSalva.setId(100L);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaSalva);

        // Act
        ReservaDTO resultado = reservaService.criar(reservaDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals("ATIVA", resultado.getStatus());
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    @DisplayName("Nao deve criar reserva quando existe sobreposicao com reserva ativa no mesmo horario")
    void naoDeveCriarReservaComConflitoExato() {
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        
        // Simula que HÁ conflito
        when(reservaRepository.existeSobreposicaoComOutra(sala, inicio, fim, StatusReserva.ATIVA, null))
                .thenReturn(true);

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            reservaService.criar(reservaDTO);
        });

        assertEquals("Já existe uma reserva ativa para esta sala que entra em conflito com o horário solicitado.", exception.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    @DisplayName("Nao deve criar reserva se a sala nao for encontrada")
    void naoDeveCriarReservaSalaNaoEncontrada() {
        when(salaRepository.findById(1L)).thenReturn(Optional.empty());

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            reservaService.criar(reservaDTO);
        });

        assertEquals("Sala não encontrada.", exception.getMessage());
    }

    @Test
    @DisplayName("Nao deve criar reserva se o usuario nao for encontrado")
    void naoDeveCriarReservaUsuarioNaoEncontrado() {
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            reservaService.criar(reservaDTO);
        });

        assertEquals("Usuário não encontrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve cancelar uma reserva pelo id")
    void deveCancelarReserva() {
        Reserva reserva = new Reserva(sala, usuario, inicio, fim);
        reserva.setId(10L);
        when(reservaRepository.findById(10L)).thenReturn(Optional.of(reserva));

        reservaService.cancelarReserva(10L);

        assertEquals(StatusReserva.CANCELADA, reserva.getStatus());
    }

    @Test
    @DisplayName("Nao deve cancelar uma reserva se o id nao existir")
    void naoDeveCancelarReservaInexistente() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            reservaService.cancelarReserva(99L);
        });

        assertEquals("Reserva com ID 99 não foi encontrada.", exception.getMessage());
    }
}
