package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.Reserva;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {
    private Long id;
    private Long salaId;
    private Long usuarioId;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private String status;

    public ReservaDTO(Reserva reserva) {
        this.id = reserva.getId();
        this.salaId = reserva.getSala().getId();
        this.usuarioId = reserva.getUsuario().getId();
        this.inicio = reserva.getInicio();
        this.fim = reserva.getFim();
        this.status = reserva.getStatus().name();
    }
}
