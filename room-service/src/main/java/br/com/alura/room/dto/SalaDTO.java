package br.com.alura.room.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import br.com.alura.room.model.Sala;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaDTO {
    private Long id;
    private String nome;
    private int capacidade;
    private boolean ativa;

    public SalaDTO(Sala sala) {
        this.id = sala.getId();
        this.nome = sala.getNome();
        this.capacidade = sala.getCapacidade();
        this.ativa = sala.isAtiva();
    }
}
