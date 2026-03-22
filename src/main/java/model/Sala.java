package model;

import exception.RegraDeNegocioException;
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

    // Adicionada a restrição unique = true para evitar nomes de salas duplicados
    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private int capacidade;

    @Column(nullable = false)
    private boolean ativa;

    // Validação de capacidade no Setter
    public void setCapacidade(int capacidade) {
        if (capacidade < 0) {
            throw new RegraDeNegocioException("A capacidade da sala não pode ser negativa.");
        }
        this.capacidade = capacidade;
    }

    // Validação para checar se a sala pode ser reservada
    public void validarSeEstaAtiva() {
        if (!this.ativa) {
            throw new RegraDeNegocioException("Não é possível realizar a reserva, pois a sala está inativa.");
        }
    }
}
