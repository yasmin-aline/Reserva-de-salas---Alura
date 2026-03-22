package controller;

import dto.SalaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import service.SalaService;

@RestController
@RequestMapping("/api/v1/salas")
public class ControllerSala {

    private final SalaService service;

    public ControllerSala(SalaService service) {
        this.service = service;
    }

    @GetMapping
    public Page<SalaDTO> listar(@PageableDefault(size = 10) Pageable paginacao) {
        return service.listar(paginacao);
    }

    @GetMapping("/{id}")
    public SalaDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SalaDTO criar(@RequestBody SalaDTO dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public SalaDTO atualizar(@PathVariable Long id, @RequestBody SalaDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        service.remover(id);
    }
}
