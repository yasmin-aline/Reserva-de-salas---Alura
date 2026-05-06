package br.com.alura.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import br.com.alura.user.model.Usuario;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;

    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
    }
}
