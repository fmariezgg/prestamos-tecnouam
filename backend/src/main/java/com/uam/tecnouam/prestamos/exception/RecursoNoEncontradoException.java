package com.uam.tecnouam.prestamos.exception;

/** Maps to HTTP 404. */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}
