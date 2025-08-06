package br.com.reservahotel.reserva_hotel.repositories;

import br.com.reservahotel.reserva_hotel.model.entities.Quarto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuartoRepository extends JpaRepository<Quarto, Long> {
    List<Quarto> findByDisponivelTrue();
}
