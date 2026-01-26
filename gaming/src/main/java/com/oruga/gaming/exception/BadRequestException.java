package com.oruga.gaming.exception;

/**
 * Excepción lanzada cuando la petición tiene datos inválidos (400)
 */
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
}
