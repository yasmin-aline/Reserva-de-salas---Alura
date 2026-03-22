package model;

import exception.RegraDeNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SalaTest {

    @Test
    @DisplayName("Deve alterar a capacidade com sucesso quando for positiva")
    void deveAlterarCapacidadeSucesso() {
        Sala sala = new Sala();
        sala.setCapacidade(10);
        
        assertEquals(10, sala.getCapacidade());
    }

    @Test
    @DisplayName("Nao deve permitir definir capacidade negativa")
    void naoDevePermitirCapacidadeNegativa() {
        Sala sala = new Sala();

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            sala.setCapacidade(-5);
        });

        assertEquals("A capacidade da sala não pode ser negativa.", exception.getMessage());
    }

    @Test
    @DisplayName("Nao deve lancar excecao ao validar sala ativa")
    void deveValidarSalaAtiva() {
        Sala sala = new Sala();
        sala.setAtiva(true);

        assertDoesNotThrow(sala::validarSeEstaAtiva);
    }

    @Test
    @DisplayName("Deve lancar excecao ao validar sala inativa")
    void deveLancarExcecaoSalaInativa() {
        Sala sala = new Sala();
        sala.setAtiva(false);

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, sala::validarSeEstaAtiva);

        assertEquals("Não é possível realizar a reserva, pois a sala está inativa.", exception.getMessage());
    }
}
