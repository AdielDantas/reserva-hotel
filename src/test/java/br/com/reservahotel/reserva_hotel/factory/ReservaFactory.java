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
        return criarReserva(1L, LocalDate.of(2025, 12, 15), LocalDate.of(2025, 12, 18),
                BigDecimal.valueOf(600.0), StatusReserva.CONFIRMADA,
                UsuarioFactory.criarUsuarioCliente(), QuartoFactory.criarQuarto());
    }

    public static Reserva criarReserva(Long id, LocalDate checkin, LocalDate checkout,
                                       BigDecimal valorTotal, StatusReserva status,
                                       Usuario usuario, Quarto quarto) {
        return new Reserva(id, checkin, checkout, valorTotal, status, usuario, quarto);
    }

    public static ReservaDTO criarReservaDTO() {
        return criarReservaDTO(1L, LocalDate.of(2025, 12, 15), LocalDate.of(2025, 12, 18),
                BigDecimal.valueOf(600.0), StatusReserva.CONFIRMADA,
                QuartoFactory.criarQuartoMinDTO(), UsuarioFactory.criarUsuarioMinDTO());
    }

    public static ReservaDTO criarReservaDTO(Long id, LocalDate checkin, LocalDate checkout,
                                             BigDecimal valorTotal, StatusReserva status,
                                             QuartoMinDTO quarto, UsuarioMinDTO usuario) {
        return new ReservaDTO(id, checkin, checkout, valorTotal, status, quarto, usuario);
    }

    public static ReservaDTO criarReservaDTOParaCriacao(Long quartoId, Long usuarioId) {
        QuartoMinDTO quartoDTO = new QuartoMinDTO();
        quartoDTO.setId(quartoId);
        quartoDTO.setValorDiaria(new BigDecimal("150.00")); // ← VALOR DIÁRIA OBRIGATÓRIO

        UsuarioMinDTO usuarioDTO = new UsuarioMinDTO();
        usuarioDTO.setId(usuarioId);

        return criarReservaDTO(null, // ID null para nova reserva
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(4),
                BigDecimal.valueOf(450.0), // Valor total calculado (3 dias * 150.00)
                StatusReserva.PENDENTE,
                quartoDTO,
                usuarioDTO);
    }

    public static ReservaMinDTO criarReservaMinDTO() {

        ReservaMinDTO reservaMinDTO = new ReservaMinDTO(1L, LocalDate.of(2025, 12, 15), LocalDate.of(2025, 12, 18), BigDecimal.valueOf(600.0), StatusReserva.CONFIRMADA);
        return reservaMinDTO;
    }
}
