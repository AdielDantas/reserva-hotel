package br.com.reservahotel.reserva_hotel.unit.repositories;

import br.com.reservahotel.reserva_hotel.factory.QuartoFactory;
import br.com.reservahotel.reserva_hotel.factory.ReservaFactory;
import br.com.reservahotel.reserva_hotel.model.entities.Quarto;
import br.com.reservahotel.reserva_hotel.model.entities.Reserva;
import br.com.reservahotel.reserva_hotel.model.enums.TipoQuarto;
import br.com.reservahotel.reserva_hotel.repositories.QuartoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.predicate;

@DataJpaTest
public class QuartoRepositoryTests {

    @Autowired
    private QuartoRepository repository;

    private Long idExistente;
    private Long idInexistente;
    private Quarto quarto;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 1000L;
        quarto = QuartoFactory.criarQuarto();
    }

    @Test
    void findByIdDeveRetornarQuartoQuandoIdForExistente() {
        Optional<Quarto> resultado = repository.findById(idExistente);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(idExistente);
    }

    @Test
    void findByIdNaoDeveRetornarReservaQuandoIdForInexistente() {
        Optional<Quarto> resultado = repository.findById(idInexistente);

        assertThat(resultado).isNotPresent();
    }

    @Test
    void findAllDeveRetornarListaDeQuartos() {
        List<Quarto> resultado = repository.findAll();

        assertThat(resultado).isNotEmpty();
        assertThat(resultado).allSatisfy(quarto -> {
            assertThat(quarto.getId()).isNotNull();
            assertThat(quarto.getValorDiaria()).isNotNull();
            assertThat(quarto.getDisponivel()).isNotNull();
            assertThat(quarto.getTipo()).isNotNull();
        });
    }

    @Test
    void findByDisponivelTrueDeveRetornarListaApenasDeQuartosDisponiveis() {
        List<Quarto> resultado = repository.findByDisponivelTrue();

        assertThat(resultado).isNotEmpty();
        assertThat(resultado).allSatisfy(quarto -> {
            assertThat(quarto.getDisponivel()).isTrue();
        });
    }

    @Test
    void findDisponiveisPorPeriodoDeveRetornarListaDeQuartosDisponiveisNoPeriodo() {
        LocalDate dataInicial = LocalDate.now().plusDays(1);
        LocalDate dataFinal = LocalDate.now().plusDays(3);

        List<Quarto> resultado = repository.findDisponiveisPorPeriodo(dataInicial, dataFinal);

        assertThat(resultado).isNotEmpty();
        assertThat(resultado).allSatisfy(quarto -> {
            assertThat(quarto.getDisponivel()).isTrue();
        });
    }

    @Test
    void findByTipoDeveRetornarListaDeQuartosQuandoExistirem() {
        List<Quarto> resultado = repository.findByTipo(TipoQuarto.ECONOMICO);

        assertThat(resultado).isNotEmpty();
    }

    @Test
    void saveDevePersistirNovoQuartoEIncrementarIdQuandoIdForNull() {
        Quarto quarto = QuartoFactory.criarQuarto();
        quarto.setId(null);
        Quarto novoQuarto = repository.save(quarto);

        assertThat(novoQuarto.getId()).isNotNull();
        assertThat(novoQuarto.getValorDiaria()).isEqualTo(quarto.getValorDiaria());
        assertThat(novoQuarto.getDisponivel()).isEqualTo(quarto.getDisponivel());
    }

    @Test
    void deleteByIdDeveDeletarQuartoQuandoIdForExistente() {
        repository.deleteById(idExistente);

        Optional<Quarto> resultado = repository.findById(idExistente);
        assertThat(resultado).isEmpty();
    }

    @Test
    void deleteByIdNaoDeveDeletarQuartoQuandoIdForInexistente() {
        repository.deleteById(idInexistente);

        Optional<Quarto> resultado = repository.findById(idInexistente);
        assertThat(resultado).isEmpty();
    }
}
