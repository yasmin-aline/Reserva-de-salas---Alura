package br.com.alura.room.exception;

public class RegraDeNegocioException extends RuntimeException {
    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
