package com.echsylon.example.rest;

/**
 * Describes an exception caused by the given authentication type being
 * unsupported, say "Basic" instead of "Bearer" etc.
 */
public class InvalidAuthenticationException extends RuntimeException {

    public InvalidAuthenticationException() {
        super();
    }

    public InvalidAuthenticationException(Throwable cause) {
        super(cause);
    }

    public InvalidAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAuthenticationException(String message) {
        super(message);
    }

}
