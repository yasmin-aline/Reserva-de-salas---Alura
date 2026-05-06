package br.com.alura.user.controller;

import br.com.alura.user.dto.UsuarioDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import br.com.alura.user.service.UsuarioService;

@RestController
@RequestMapping("/api/v1/usuarios")
@SecurityRequirement(name = "basicAuth")
public class ControllerUsuario {

    private final UsuarioService service;

    public ControllerUsuario(UsuarioService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<UsuarioDTO> listar(@PageableDefault(size = 10) Pageable paginacao) {
        return service.listar(paginacao);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UsuarioDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO criar(@RequestBody UsuarioDTO dto) {
        return service.criar(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public UsuarioDTO atualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        return service.atualizar(id, dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        service.remover(id);
    }
}
