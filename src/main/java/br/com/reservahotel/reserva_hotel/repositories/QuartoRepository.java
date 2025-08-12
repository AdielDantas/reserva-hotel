package br.com.reservahotel.reserva_hotel.repositories;

import br.com.reservahotel.reserva_hotel.model.entities.Quarto;
import br.com.reservahotel.reserva_hotel.model.enums.TipoQuarto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface QuartoRepository extends JpaRepository<Quarto, Long> {

    List<Quarto> findByDisponivelTrue();

    @Query("""
        SELECT q FROM Quarto q 
        WHERE q.disponivel = TRUE
        AND q.id NOT IN (
            SELECT r.quarto.id FROM Reserva r
            WHERE (r.checkin <= :dataFinal AND r.checkout >= :dataInicial)
        )
    """)
    List<Quarto> findDisponiveisPorPeriodo(LocalDate dataInicial, LocalDate dataFinal);

    List<Quarto> findByTipo(TipoQuarto tipo);
}

