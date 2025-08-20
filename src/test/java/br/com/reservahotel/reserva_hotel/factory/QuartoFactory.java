package br.com.reservahotel.reserva_hotel.factory;

import br.com.reservahotel.reserva_hotel.model.entities.Quarto;
import br.com.reservahotel.reserva_hotel.model.enums.TipoQuarto;

import java.math.BigDecimal;
import java.util.ArrayList;

public class QuartoFactory {

    public static Quarto criarQuarto() {
        Quarto quarto = new Quarto(1L, BigDecimal.valueOf(200.0), Boolean.TRUE, TipoQuarto.ECONOMICO, new ArrayList<>());
        return quarto;
    }
}
