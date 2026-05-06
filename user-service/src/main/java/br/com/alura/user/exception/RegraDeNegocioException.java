package br.com.alura.user.exception;

public class RegraDeNegocioException extends RuntimeException {
    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
