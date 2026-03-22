package model;

import exception.RegraDeNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservaTest {

    @Test
    @DisplayName("Deve criar uma reserva com sucesso quando os dados forem validos e a sala estiver ativa")
    void deveCriarReservaComSucesso() {
        Sala sala = new Sala(1L, "Sala A", 10, true);
        Usuario usuario = new Usuario(1L, "João", "joao@email.com");
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = inicio.plusHours(2);

        Reserva reserva = new Reserva(sala, usuario, inicio, fim);

        assertNotNull(reserva);
        assertEquals(StatusReserva.ATIVA, reserva.getStatus());
        assertEquals(sala, reserva.getSala());
    }

    @Test
    @DisplayName("Nao deve criar reserva quando inicio for depois do fim")
    void naoDeveCriarReservaInicioDepoisFim() {
        Sala sala = new Sala(1L, "Sala A", 10, true);
        Usuario usuario = new Usuario(1L, "João", "joao@email.com");
        LocalDateTime inicio = LocalDateTime.now().plusDays(1).plusHours(2);
        LocalDateTime fim = LocalDateTime.now().plusDays(1);

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            new Reserva(sala, usuario, inicio, fim);
        });

        assertEquals("O horário de início da reserva deve ser anterior ao horário de término.", exception.getMessage());
    }

    @Test
    @DisplayName("Nao deve criar reserva se as datas forem nulas")
    void naoDeveCriarReservaDatasNulas() {
        Sala sala = new Sala(1L, "Sala A", 10, true);
        Usuario usuario = new Usuario(1L, "João", "joao@email.com");

        assertThrows(RegraDeNegocioException.class, () -> new Reserva(sala, usuario, null, LocalDateTime.now()));
        assertThrows(RegraDeNegocioException.class, () -> new Reserva(sala, usuario, LocalDateTime.now(), null));
    }

    @Test
    @DisplayName("Nao deve criar reserva se a sala estiver inativa")
    void naoDeveCriarReservaSalaInativa() {
        Sala sala = new Sala(1L, "Sala A", 10, false);
        Usuario usuario = new Usuario(1L, "João", "joao@email.com");
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = inicio.plusHours(2);

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            new Reserva(sala, usuario, inicio, fim);
        });

        assertEquals("Não é possível realizar a reserva, pois a sala está inativa.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve cancelar uma reserva ativa com sucesso")
    void deveCancelarReservaAtiva() {
        Sala sala = new Sala(1L, "Sala A", 10, true);
        Usuario usuario = new Usuario(1L, "João", "joao@email.com");
        Reserva reserva = new Reserva(sala, usuario, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2));

        reserva.cancelar();

        assertEquals(StatusReserva.CANCELADA, reserva.getStatus());
    }

    @Test
    @DisplayName("Nao deve permitir cancelar uma reserva que ja esta cancelada")
    void naoDeveCancelarReservaJaCancelada() {
        Sala sala = new Sala(1L, "Sala A", 10, true);
        Usuario usuario = new Usuario(1L, "João", "joao@email.com");
        Reserva reserva = new Reserva(sala, usuario, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2));

        reserva.cancelar(); // Primeiro cancelamento
        
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, reserva::cancelar);

        assertEquals("A reserva já se encontra cancelada.", exception.getMessage());
    }
}
