package br.com.softdesign.votacao.exception;

public class VotoInvalidoException extends RuntimeException{

    public VotoInvalidoException(String message){
        super(message);
    }
}
