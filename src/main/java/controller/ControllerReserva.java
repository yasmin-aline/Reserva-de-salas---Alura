package controller;

import dto.ReservaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import service.ReservaService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/reservas")
public class ControllerReserva {

    private final ReservaService service;

    public ControllerReserva(ReservaService service) {
        this.service = service;
    }

    @GetMapping
    public Page<ReservaDTO> listar(@PageableDefault(size = 10) Pageable paginacao) {
        return service.listar(paginacao);
    }

    @GetMapping("/sala/{salaId}")
    public Page<ReservaDTO> listarPorSala(@PathVariable Long salaId, @PageableDefault(size = 10) Pageable paginacao) {
        return service.listarPorSala(salaId, paginacao);
    }
    
    @GetMapping("/intervalo")
    public Page<ReservaDTO> buscarPorIntervalo(
            @RequestParam Long salaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @PageableDefault(size = 10) Pageable paginacao) {
        return service.buscarPorIntervalo(salaId, inicio, fim, paginacao);
    }

    @GetMapping("/{id}")
    public ReservaDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaDTO criar(@RequestBody ReservaDTO dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public ReservaDTO atualizar(@PathVariable Long id, @RequestBody ReservaDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelar(@PathVariable Long id) {
        service.cancelarReserva(id);
    }
}
