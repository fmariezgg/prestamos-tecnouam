package com.uam.tecnouam.prestamos.exception;

/** Bad login. Maps to HTTP 401. */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Usuario o contraseña incorrectos");
    }
}
