package model;

import exception.RegraDeNegocioException;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column(nullable = false)
    private LocalDateTime fim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusReserva status;

    // Construtor para criar uma nova reserva aplicando as regras de negócio
    public Reserva(Sala sala, Usuario usuario, LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            throw new RegraDeNegocioException("As datas de início e fim da reserva são obrigatórias.");
        }
        if (inicio.isAfter(fim) || inicio.isEqual(fim)) {
            throw new RegraDeNegocioException("O horário de início da reserva deve ser anterior ao horário de término.");
        }
        
        // Regra: Sala inativa lança exceção
        sala.validarSeEstaAtiva();

        this.sala = sala;
        this.usuario = usuario;
        this.inicio = inicio;
        this.fim = fim;
        this.status = StatusReserva.ATIVA;
    }

    // Regra: Método cancelar() na Reserva que muda o status
    public void cancelar() {
        if (this.status == StatusReserva.CANCELADA) {
            throw new RegraDeNegocioException("A reserva já se encontra cancelada.");
        }
        this.status = StatusReserva.CANCELADA;
    }
}
