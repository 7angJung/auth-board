package com.jupeter.authboard.domain.user.application.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) { super(message); }
}
