package com.jupeter.authboard.global.error;

import com.jupeter.authboard.domain.board.application.exception.BoardForbiddenException;
import com.jupeter.authboard.domain.board.application.exception.BoardNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BoardForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleForbidden(BoardForbiddenException e) {
        return e.getMessage();
    }

    @ExceptionHandler(BoardNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(BoardNotFoundException e) {
        return e.getMessage();
    }
}