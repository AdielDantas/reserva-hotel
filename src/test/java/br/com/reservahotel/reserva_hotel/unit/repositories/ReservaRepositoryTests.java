package br.com.reservahotel.reserva_hotel.unit.repositories;

import br.com.reservahotel.reserva_hotel.factory.ReservaFactory;
import br.com.reservahotel.reserva_hotel.model.entities.Reserva;
import br.com.reservahotel.reserva_hotel.repositories.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ReservaRepositoryTests {

    @Autowired
    ReservaRepository repository;

    private Long idExistente;
    private Long idInexistente;
    private Long idUsuarioExistente;
    private Long idUsuarioInexistente;
    private Reserva reserva;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 1000L;
        idUsuarioExistente = 2L;
        idUsuarioInexistente = 1000L;
        reserva = ReservaFactory.criarReserva();
    }

    @Test
    void findByIdDeveRetornarReservaQuandoIdForExistente() {
        Optional<Reserva> resultado = repository.findById(idExistente);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(idExistente);
    }

    @Test
    void findByIdNaoDeveRetornarReservaQuandoIdForInexistente() {
        Optional<Reserva> resultado = repository.findById(idInexistente);

        assertThat(resultado).isNotPresent();
    }

    @Test
    void findByUsuarioIdDeveRetornarListaDeReservasQuandoIdForExistente() {
        List<Reserva> resultado = repository.findByUsuarioId(idUsuarioExistente);

        assertThat(resultado).isNotEmpty();
    }

    @Test
    void findByUsuarioIdNaoDeveRetornarListaDeReservasQuandoIdForInexistente() {
        List<Reserva> resultado = repository.findByUsuarioId(idUsuarioInexistente);

        assertThat(resultado).isEmpty();
    }

    @Test
    void saveDevePersistirNovaReservaEIncrementarIdQuandoIdForNull() {
        Reserva reserva = ReservaFactory.criarReserva();
        reserva.setId(null);
        Reserva novaReserva = repository.save(reserva);

        assertThat(novaReserva.getId()).isNotNull();
        assertThat(novaReserva.getCheckin()).isEqualTo(reserva.getCheckin());
        assertThat(novaReserva.getCheckout()).isEqualTo(reserva.getCheckout());
        assertThat(novaReserva.getValorTotal()).isEqualTo(reserva.getValorTotal());
        assertThat(novaReserva.getStatus()).isEqualTo(reserva.getStatus());
        assertThat(novaReserva.getCheckin()).isEqualTo(reserva.getCheckin());
    }

    @Test
    void deleteByIdDeveDeletarReservaQuandoIdForExistente() {
        repository.deleteById(idExistente);

        Optional<Reserva> resultado = repository.findById(idExistente);
        assertThat(resultado).isEmpty();
    }

    @Test
    void deleteByIdNaoDeveDeletarReservaQuandoIdForInexistente() {
        repository.deleteById(idInexistente);

        Optional<Reserva> resultado = repository.findById(idInexistente);
        assertThat(resultado).isEmpty();
    }
}
