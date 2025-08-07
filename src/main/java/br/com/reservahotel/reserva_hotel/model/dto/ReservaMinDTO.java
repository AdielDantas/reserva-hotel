package br.com.reservahotel.reserva_hotel.model.dto;

import br.com.reservahotel.reserva_hotel.model.enums.StatusReserva;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaMinDTO {

    private Long id;
    private LocalDate checkin;
    private LocalDate checkout;
    private BigDecimal valorTotal;
    private StatusReserva status;
}
