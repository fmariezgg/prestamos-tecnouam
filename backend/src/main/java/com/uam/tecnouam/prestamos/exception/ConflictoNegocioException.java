package com.uam.tecnouam.prestamos.exception;

/** Business rule violation. Maps to HTTP 409. */
public class ConflictoNegocioException extends RuntimeException {

    public ConflictoNegocioException(String message) {
        super(message);
    }
}
