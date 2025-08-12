package br.com.reservahotel.reserva_hotel.repositories;

import br.com.reservahotel.reserva_hotel.model.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuarioId(Long id);

    @Query("SELECT r FROM Reserva r JOIN r.usuario u WHERE u.email = :email")
    List<Reserva> findByUsuarioEmail(String email);
}
