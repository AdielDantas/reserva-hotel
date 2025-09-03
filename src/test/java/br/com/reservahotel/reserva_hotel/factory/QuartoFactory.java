package br.com.reservahotel.reserva_hotel.factory;

import br.com.reservahotel.reserva_hotel.model.dto.QuartoDTO;
import br.com.reservahotel.reserva_hotel.model.dto.QuartoMinDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Quarto;
import br.com.reservahotel.reserva_hotel.model.enums.TipoQuarto;

import java.math.BigDecimal;
import java.util.ArrayList;

public class QuartoFactory {

    public static Quarto criarQuarto() {
        Quarto quarto = new Quarto(1L, BigDecimal.valueOf(200.0), Boolean.TRUE, TipoQuarto.ECONOMICO, new ArrayList<>());
        return quarto;
    }

    public static QuartoDTO criarQuartoDTO() {
        QuartoDTO quartoDTO = new QuartoDTO(1L, BigDecimal.valueOf(200.0), Boolean.TRUE, TipoQuarto.ECONOMICO, new ArrayList<>());
        return quartoDTO;
    }

    public static QuartoMinDTO criarQuartoMinDTO() {
        QuartoMinDTO quartoMinDTO = new QuartoMinDTO(1L, BigDecimal.valueOf(200.0), Boolean.TRUE, TipoQuarto.ECONOMICO);
        return quartoMinDTO;
    }

    public static QuartoDTO criarQuartoDTOAtualizacao() {
        QuartoDTO quartoDTO = new QuartoDTO(null, BigDecimal.valueOf(350.00), Boolean.FALSE, TipoQuarto.LUXO, new ArrayList<>());
        return quartoDTO;
    }
}
