package br.com.reservahotel.reserva_hotel.repositories;

import br.com.reservahotel.reserva_hotel.model.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuarioId();
}
