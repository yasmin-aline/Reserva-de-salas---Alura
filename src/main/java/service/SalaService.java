package service;

import dto.SalaDTO;
import exception.RegraDeNegocioException;
import model.Sala;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.SalaRepository;

import java.util.Optional;

@Service
public class SalaService {

    private final SalaRepository repository;

    public SalaService(SalaRepository repository) {
        this.repository = repository;
    }

    public Page<SalaDTO> listar(Pageable paginacao) {
        return repository.findAll(paginacao).map(SalaDTO::new);
    }

    public SalaDTO buscarPorId(Long id) {
        Sala sala = repository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Sala com ID " + id + " não encontrada."));
        return new SalaDTO(sala);
    }

    @Transactional
    public SalaDTO criar(SalaDTO dto) {
        validarNomeUnico(dto.getNome(), null);
        
        Sala sala = new Sala();
        sala.setNome(dto.getNome());
        sala.setCapacidade(dto.getCapacidade());
        sala.setAtiva(dto.isAtiva());
        
        Sala salva = repository.save(sala);
        return new SalaDTO(salva);
    }

    @Transactional
    public SalaDTO atualizar(Long id, SalaDTO dto) {
        Sala sala = repository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Sala com ID " + id + " não encontrada."));
        
        validarNomeUnico(dto.getNome(), id);
        
        sala.setNome(dto.getNome());
        sala.setCapacidade(dto.getCapacidade());
        sala.setAtiva(dto.isAtiva());
        
        return new SalaDTO(sala);
    }

    @Transactional
    public void remover(Long id) {
        Sala sala = repository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Sala com ID " + id + " não encontrada."));
        repository.delete(sala);
    }
    
    private void validarNomeUnico(String nome, Long salaId) {
        Optional<Sala> salaExistente = repository.findByNome(nome);
        if (salaExistente.isPresent() && !salaExistente.get().getId().equals(salaId)) {
            throw new RegraDeNegocioException("Já existe uma sala cadastrada com o nome " + nome);
        }
    }
}
