package br.com.reservahotel.reserva_hotel.model.dto;

import br.com.reservahotel.reserva_hotel.model.enums.TipoQuarto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuartoDTO {

    private Long id;

    @NotNull(message = "O valor da diária é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = false, message = "O valor da diária deve ser maior que zero.")
    private BigDecimal valorDiaria;

    @NotNull(message = "A disponibilidade é obrigatória.")
    private Boolean disponivel;

    @NotNull(message = "O tipo do quarto é obrigatório.")
    private TipoQuarto tipo;

    private List<ReservaMinDTO> reservas = new ArrayList<>();
}
