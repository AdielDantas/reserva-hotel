package br.com.reservahotel.reserva_hotel.exceptions;

public class DataBaseException extends RuntimeException{

    public DataBaseException(String msg) {
        super(msg);
    }
}
