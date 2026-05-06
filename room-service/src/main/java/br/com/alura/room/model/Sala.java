package br.com.alura.room.model;

import br.com.alura.room.exception.RegraDeNegocioException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sala")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private int capacidade;

    @Column(nullable = false)
    private boolean ativa;

    public void setCapacidade(int capacidade) {
        if (capacidade < 0) {
            throw new RegraDeNegocioException("A capacidade da sala não pode ser negativa.");
        }
        this.capacidade = capacidade;
    }

    public void validarSeEstaAtiva() {
        if (!this.ativa) {
            throw new RegraDeNegocioException("Não é possível realizar a reserva, pois a sala está inativa.");
        }
    }
}
