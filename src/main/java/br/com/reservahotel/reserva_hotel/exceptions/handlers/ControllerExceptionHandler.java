package br.com.reservahotel.reserva_hotel.exceptions.handlers;

import br.com.reservahotel.reserva_hotel.exceptions.DataBaseException;
import br.com.reservahotel.reserva_hotel.exceptions.ResourceNotFoundException;
import br.com.reservahotel.reserva_hotel.exceptions.RespostaErroApi;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<RespostaErroApi> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        RespostaErroApi respostaErroApi = new RespostaErroApi(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(respostaErroApi);
    }

    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<RespostaErroApi> database(DataBaseException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        RespostaErroApi respostaErroApi = new RespostaErroApi(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(respostaErroApi);
    }
}
