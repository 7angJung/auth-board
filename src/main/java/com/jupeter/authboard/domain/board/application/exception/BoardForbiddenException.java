package com.jupeter.authboard.domain.board.application.exception;

public class BoardForbiddenException extends RuntimeException {
    public BoardForbiddenException(String message) {
        super(message);
    }
}