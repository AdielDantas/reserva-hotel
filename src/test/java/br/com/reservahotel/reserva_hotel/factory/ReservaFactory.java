package br.com.reservahotel.reserva_hotel.factory;

import br.com.reservahotel.reserva_hotel.model.dto.QuartoMinDTO;
import br.com.reservahotel.reserva_hotel.model.dto.ReservaDTO;
import br.com.reservahotel.reserva_hotel.model.dto.ReservaMinDTO;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioMinDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Quarto;
import br.com.reservahotel.reserva_hotel.model.entities.Reserva;
import br.com.reservahotel.reserva_hotel.model.entities.Usuario;
import br.com.reservahotel.reserva_hotel.model.enums.StatusReserva;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class ReservaFactory {

    public static Reserva criarReserva() {
        Usuario usuario = UsuarioFactory.criarUsuarioCliente();
        Quarto quarto = QuartoFactory.criarQuarto();

        Reserva reserva = new Reserva(1L, LocalDate.of(2025, 12, 15), LocalDate.of(2025, 12, 18), BigDecimal.valueOf(600.0), StatusReserva.CONFIRMADA, usuario, quarto);
        return reserva;
    }

    public static ReservaDTO criarReservaDTO() {
        UsuarioMinDTO usuarioMinDTO = UsuarioFactory.criarUsuarioMinDTO();
        QuartoMinDTO quartoMinDTO = QuartoFactory.criarQuartoMinDTO();

        ReservaDTO reservaDTO = new ReservaDTO(1L, LocalDate.of(2025, 12, 15), LocalDate.of(2025, 12, 18), BigDecimal.valueOf(600.0), StatusReserva.CONFIRMADA, quartoMinDTO, usuarioMinDTO);
        return reservaDTO;
    }

    public static ReservaMinDTO criarReservaMinDTO() {

        ReservaMinDTO reservaMinDTO = new ReservaMinDTO(1L, LocalDate.of(2025, 12, 15), LocalDate.of(2025, 12, 18), BigDecimal.valueOf(600.0), StatusReserva.CONFIRMADA);
        return reservaMinDTO;
    }
}
