package service;

import dto.UsuarioDTO;
import exception.RegraDeNegocioException;
import model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Page<UsuarioDTO> listar(Pageable paginacao) {
        return repository.findAll(paginacao).map(UsuarioDTO::new);
    }

    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário com ID " + id + " não encontrado."));
        return new UsuarioDTO(usuario);
    }

    @Transactional
    public UsuarioDTO criar(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        
        Usuario salvo = repository.save(usuario);
        return new UsuarioDTO(salvo);
    }

    @Transactional
    public UsuarioDTO atualizar(Long id, UsuarioDTO dto) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário com ID " + id + " não encontrado."));
        
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        
        return new UsuarioDTO(usuario);
    }

    @Transactional
    public void remover(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário com ID " + id + " não encontrado."));
        repository.delete(usuario);
    }
}
