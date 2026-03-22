package service;

import dto.SalaDTO;
import exception.RegraDeNegocioException;
import model.Sala;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.SalaRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaServiceTest {

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private SalaService salaService;

    private SalaDTO salaDTO;

    @BeforeEach
    void setUp() {
        salaDTO = new SalaDTO();
        salaDTO.setNome("Sala VIP");
        salaDTO.setCapacidade(20);
        salaDTO.setAtiva(true);
    }

    @Test
    @DisplayName("Deve criar uma nova sala quando o nome nao existir")
    void deveCriarSalaComSucesso() {
        when(salaRepository.findByNome("Sala VIP")).thenReturn(Optional.empty());
        
        Sala salaSalva = new Sala(1L, "Sala VIP", 20, true);
        when(salaRepository.save(any(Sala.class))).thenReturn(salaSalva);

        SalaDTO resultado = salaService.criar(salaDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Sala VIP", resultado.getNome());
        verify(salaRepository, times(1)).save(any(Sala.class));
    }

    @Test
    @DisplayName("Nao deve criar uma sala se o nome ja existir")
    void naoDeveCriarSalaNomeDuplicado() {
        Sala salaExistente = new Sala(2L, "Sala VIP", 10, true);
        when(salaRepository.findByNome("Sala VIP")).thenReturn(Optional.of(salaExistente));

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            salaService.criar(salaDTO);
        });

        assertEquals("Já existe uma sala cadastrada com o nome Sala VIP", exception.getMessage());
        verify(salaRepository, never()).save(any(Sala.class));
    }

    @Test
    @DisplayName("Deve atualizar sala com sucesso mantendo o mesmo nome")
    void deveAtualizarSalaMesmoNome() {
        Sala salaExistente = new Sala(1L, "Sala VIP", 10, true);
        when(salaRepository.findById(1L)).thenReturn(Optional.of(salaExistente));
        
        // Simula a busca pelo nome da propria sala, nao deve dar exceção
        when(salaRepository.findByNome("Sala VIP")).thenReturn(Optional.of(salaExistente));

        SalaDTO novaAparencia = new SalaDTO();
        novaAparencia.setNome("Sala VIP");
        novaAparencia.setCapacidade(30);
        novaAparencia.setAtiva(false);

        SalaDTO resultado = salaService.atualizar(1L, novaAparencia);

        assertEquals(30, resultado.getCapacidade());
        assertFalse(resultado.isAtiva());
    }

    @Test
    @DisplayName("Nao deve atualizar sala para nome que ja pertence a outra sala")
    void naoDeveAtualizarSalaNomeOutraSala() {
        Sala salaSendoEditada = new Sala(1L, "Sala Velha", 10, true);
        when(salaRepository.findById(1L)).thenReturn(Optional.of(salaSendoEditada));
        
        Sala outraSala = new Sala(2L, "Sala VIP", 20, true);
        when(salaRepository.findByNome("Sala VIP")).thenReturn(Optional.of(outraSala));

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class, () -> {
            salaService.atualizar(1L, salaDTO); // salaDTO tem nome "Sala VIP"
        });

        assertEquals("Já existe uma sala cadastrada com o nome Sala VIP", exception.getMessage());
    }
}
