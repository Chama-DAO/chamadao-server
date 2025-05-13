package com.chama.chamadao_server.exceptions;

public class ChamaException extends RuntimeException {
    public ChamaException(String message) {
        super(message);
    }

    public ChamaException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
