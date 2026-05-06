package br.com.alura.booking.model;

import br.com.alura.booking.exception.RegraDeNegocioException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reserva")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sala_id", nullable = false)
    private Long salaId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column(nullable = false)
    private LocalDateTime fim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusReserva status;

    public Reserva(Long salaId, Long usuarioId, LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            throw new RegraDeNegocioException("As datas de início e fim da reserva são obrigatórias.");
        }
        if (inicio.isAfter(fim) || inicio.isEqual(fim)) {
            throw new RegraDeNegocioException("O horário de início da reserva deve ser anterior ao horário de término.");
        }

        this.salaId = salaId;
        this.usuarioId = usuarioId;
        this.inicio = inicio;
        this.fim = fim;
        this.status = StatusReserva.ATIVA;
    }

    public void cancelar() {
        if (this.status == StatusReserva.CANCELADA) {
            throw new RegraDeNegocioException("A reserva já se encontra cancelada.");
        }
        this.status = StatusReserva.CANCELADA;
    }
}
