package br.com.reservahotel.reserva_hotel.exceptions.handlers;

import br.com.reservahotel.reserva_hotel.exceptions.*;
import br.com.reservahotel.reserva_hotel.exceptions.IllegalArgumentException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<RespostaErroApi> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        RespostaErroApi erro = new RespostaErroApi(
                Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<RespostaErroApi> database(DataBaseException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        RespostaErroApi erro = new RespostaErroApi(
                Instant.now(), status.value(),
                e.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespostaErroApi> illegalArgument(IllegalArgumentException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        RespostaErroApi erro = new RespostaErroApi(
                Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RespostaErroApi> methodArgumentType(MethodArgumentTypeMismatchException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        RespostaErroApi erro = new RespostaErroApi(
                Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaErroApi> methodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        RespostaErroValidation erro = new RespostaErroValidation(
                Instant.now(), status.value(),
                "Erro na validação. Dados inválidos",
                request.getRequestURI());

        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            erro.addError(f.getField(), f.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<RespostaErroApi> handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;
        RespostaErroApi erro = new RespostaErroApi(
                Instant.now(),
                status.value(),
                "Erro na reserva: " + e.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<RespostaErroApi> forbidden(ForbiddenException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.FORBIDDEN;
        RespostaErroApi erro = new RespostaErroApi(
                Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(status).body(erro);
    }
}