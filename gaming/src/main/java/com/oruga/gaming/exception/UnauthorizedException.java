package com.oruga.gaming.exception;

/**
 * Excepción lanzada cuando el usuario no está autorizado (401)
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
}
