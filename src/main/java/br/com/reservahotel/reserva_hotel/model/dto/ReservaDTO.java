package br.com.reservahotel.reserva_hotel.model.dto;

import br.com.reservahotel.reserva_hotel.model.enums.StatusReserva;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {

    private Long id;

    @NotNull(message = "A data de check-in é obrigatória.")
    @FutureOrPresent(message = "A data de check-in não pode ser no passado.")
    private LocalDate checkin;

    @NotNull(message = "A data de check-out é obrigatória.")
    @Future(message = "A data de check-out deve ser no futuro.")
    private LocalDate checkout;

    @NotNull(message = "O valor total é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = false, message = "O valor total deve ser maior que zero.")
    private BigDecimal valorTotal;

    @NotNull(message = "O status da reserva é obrigatório.")
    private StatusReserva status;

    @NotNull(message = "O quarto é obrigatório.")
    @Valid
    private QuartoMinDTO quarto;

    @NotNull(message = "O usuário é obrigatório.")
    @Valid
    private UsuarioMinDTO usuario;
}
