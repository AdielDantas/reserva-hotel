package br.com.reservahotel.reserva_hotel.model.dto;

import br.com.reservahotel.reserva_hotel.model.enums.TipoQuarto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuartoMinDTO {

    private Long id;
    private BigDecimal valorDiaria;
    private Boolean disponivel;
    private TipoQuarto tipo;
}
