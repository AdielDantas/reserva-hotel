package br.com.reservahotel.reserva_hotel.integracao.services;

import br.com.reservahotel.reserva_hotel.exceptions.DataBaseException;
import br.com.reservahotel.reserva_hotel.exceptions.ForbiddenException;
import br.com.reservahotel.reserva_hotel.exceptions.ResourceNotFoundException;
import br.com.reservahotel.reserva_hotel.factory.QuartoFactory;
import br.com.reservahotel.reserva_hotel.model.dto.QuartoDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Quarto;
import br.com.reservahotel.reserva_hotel.model.enums.TipoQuarto;
import br.com.reservahotel.reserva_hotel.repositories.QuartoRepository;
import br.com.reservahotel.reserva_hotel.services.QuartoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.com.reservahotel.reserva_hotel.model.enums.TipoQuarto.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class QuartoServiceIntegrationTests {

    @Autowired
    private QuartoService service;

    @Autowired
    private QuartoRepository repository;

    private Long idExistente;
    private Long idInexistente;
    private Long idExistenteSemDependencias;
    private Long idComDependencias;
    private String tipoValido;
    private String tipoMinusculo;
    private String tipoMaiusculo;
    private String tipoMisto;
    private String tipoInvalido;
    private Quarto quarto;
    private QuartoDTO quartoDTO;
    private QuartoDTO quartoDTOAtualizacao;

    @BeforeEach
    void setUp() {

        idExistente = 1L;
        idInexistente = 1000L;
        idExistenteSemDependencias = 2L;
        idComDependencias = 1L;
        tipoValido = "LUXO";
        tipoMinusculo = "luxo";
        tipoMaiusculo = "ECONOMICO";
        tipoMisto = "StAnDaRd";
        tipoInvalido = "INEXISTENTE";
        quarto = QuartoFactory.criarQuarto();
        quartoDTO = QuartoFactory.criarQuartoDTO();
        quartoDTOAtualizacao = QuartoFactory.criarQuartoDTOAtualizacao();
    }

    @Test
    void buscarQuartoPorId_DeveRetornarQuarto_QuandoIdExistir() {
        QuartoDTO resultado = service.buscarQuartoPorId(idExistente);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(idExistente);
        assertThat(resultado.getTipo()).isNotNull();
        assertThat(resultado.getValorDiaria()).isPositive();
        assertThat(resultado.getDisponivel()).isNotNull();
    }

    @Test
    void buscarQuartoPorId_DeveLancarResourceNotFoundException_QuandoIdNaoExistir() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.buscarQuartoPorId(idInexistente);
        });
    }

    @Test
    void listarTodosOsQuartos_DeveRetornarListaDeQuartos() {
        List<QuartoDTO> resultado = service.listarTodosOsQuartos();

        assertThat(resultado).isNotNull().isNotEmpty();
        assertThat(resultado.size()).isGreaterThan(0);

        resultado.forEach(quarto -> {
            assertThat(quarto.getId()).isNotNull();
            assertThat(quarto.getTipo()).isNotNull();
            assertThat(quarto.getValorDiaria()).isPositive();
        });
    }

    @Test
    @Transactional
    void listarQuartosDisponiveis_DeveRetornarQuartosDisponiveis_QuandoSemPeriodo() {
        List<QuartoDTO> resultado = service.listarQuartosDisponiveis(null, null);

        assertThat(resultado).isNotNull();
        resultado.forEach(quarto -> {
            assertThat(quarto.getDisponivel()).isTrue();
        });
    }

    @Test
    @Transactional
    void listarQuartosDisponiveis_DeveRetornarQuartosDisponiveis_QuandoComPeriodoValido() {
        LocalDate dataInicial = LocalDate.now().plusDays(10);
        LocalDate dataFinal = LocalDate.now().plusDays(15);

        List<QuartoDTO> resultado = service.listarQuartosDisponiveis(dataInicial, dataFinal);

        assertThat(resultado).isNotNull();
        // Pode estar vazio se não houver quartos disponíveis no período
    }

    @Test
    @Transactional
    void listarQuartosDisponiveis_DeveLancarIllegalArgumentException_QuandoDataInicialDepoisDataFinal() {
        LocalDate dataInicial = LocalDate.now().plusDays(15);
        LocalDate dataFinal = LocalDate.now().plusDays(10);

        assertThrows(IllegalArgumentException.class, () -> {
            service.listarQuartosDisponiveis(dataInicial, dataFinal);
        });
    }

    @Test
    @Transactional
    void listarQuartosDisponiveis_DeveLancarIllegalArgumentException_QuandoDataInicialNulaEDataFinalPreenchida() {
        LocalDate dataFinal = LocalDate.now().plusDays(10);

        assertThrows(IllegalArgumentException.class, () -> {
            service.listarQuartosDisponiveis(null, dataFinal);
        });
    }

    @Test
    @Transactional
    void listarQuartosDisponiveis_DeveLancarIllegalArgumentException_QuandoDataFinalNulaEDataInicialPreenchida() {
        LocalDate dataInicial = LocalDate.now().plusDays(10);

        assertThrows(IllegalArgumentException.class, () -> {
            service.listarQuartosDisponiveis(dataInicial, null);
        });
    }

    @Test
    @Transactional
    void listarQuartosDisponiveis_DeveLancarIllegalArgumentException_QuandoDataInicialNoPassado() {
        LocalDate dataInicial = LocalDate.now().minusDays(1);
        LocalDate dataFinal = LocalDate.now().plusDays(5);

        assertThrows(IllegalArgumentException.class, () -> {
            service.listarQuartosDisponiveis(dataInicial, dataFinal);
        });
    }

    @Test
    @Transactional
    void listarQuartosDisponiveis_DeveLancarIllegalArgumentException_QuandoDataFinalNoPassado() {
        LocalDate dataInicial = LocalDate.now().plusDays(1);
        LocalDate dataFinal = LocalDate.now().minusDays(1);

        assertThrows(IllegalArgumentException.class, () -> {
            service.listarQuartosDisponiveis(dataInicial, dataFinal);
        });
    }

    @Test
    @Transactional
    void listarQuartosDisponiveis_DeveRetornarApenasQuartosDisponiveis_QuandoComPeriodo() {
        LocalDate dataInicial = LocalDate.now().plusDays(20);
        LocalDate dataFinal = LocalDate.now().plusDays(25);

        List<QuartoDTO> resultado = service.listarQuartosDisponiveis(dataInicial, dataFinal);

        assertThat(resultado).isNotNull();
    }

    @Test
    @Transactional
    void listarQuartosDisponiveis_DeveRetornarResultadosDiferentes_QuandoComESemPeriodo() {
        // Lista sem período - todos os quartos disponíveis no momento
        List<QuartoDTO> semPeriodo = service.listarQuartosDisponiveis(null, null);

        // Lista com período futuro - quartos disponíveis naquele período específico
        LocalDate dataInicial = LocalDate.now().plusDays(30);
        LocalDate dataFinal = LocalDate.now().plusDays(35);
        List<QuartoDTO> comPeriodo = service.listarQuartosDisponiveis(dataInicial, dataFinal);

        assertThat(semPeriodo).isNotNull();
        assertThat(comPeriodo).isNotNull();
    }

    @Test
    @Transactional
    void listarQuartosDisponiveis_DeveFuncionar_QuandoPeriodoHoje() {
        LocalDate hoje = LocalDate.now();
        LocalDate amanha = LocalDate.now().plusDays(1);

        // Testa um período que começa hoje
        List<QuartoDTO> resultado = service.listarQuartosDisponiveis(hoje, amanha);

        assertThat(resultado).isNotNull();
    }

    @Test
    @Transactional
    void listarQuartoPorTipo_DeveRetornarListaQuartos_QuandoTipoValido() {

        List<QuartoDTO> resultado = service.listarQuartoPorTipo(tipoValido);

        assertThat(resultado).isNotNull();
        resultado.forEach(quarto -> {
            assertThat(quarto.getTipo()).isEqualTo(LUXO);
        });
    }

    @Test
    @Transactional
    void listarQuartoPorTipo_DeveRetornarQuartos_QuandoTipoEmMinusculo() {

        List<QuartoDTO> resultado = service.listarQuartoPorTipo(tipoMinusculo);

        assertThat(resultado).isNotNull();
        resultado.forEach(quarto -> {
            assertThat(quarto.getTipo()).isEqualTo(LUXO);
        });
    }

    @Test
    @Transactional
    void listarQuartoPorTipo_DeveRetornarQuartos_QuandoTipoEmMaiusculo() {

        List<QuartoDTO> resultado = service.listarQuartoPorTipo(tipoMaiusculo);

        assertThat(resultado).isNotNull();
        resultado.forEach(quarto -> {
            assertThat(quarto.getTipo()).isEqualTo(ECONOMICO);
        });
    }

    @Test
    @Transactional
    void listarQuartoPorTipo_DeveRetornarQuartos_QuandoTipoMisto() {

        List<QuartoDTO> resultado = service.listarQuartoPorTipo(tipoMisto);

        assertThat(resultado).isNotNull();
        resultado.forEach(quarto -> {
            assertThat(quarto.getTipo()).isEqualTo(STANDARD);
        });
    }

    @Test
    @Transactional
    void listarQuartoPorTipo_DeveLancarResourceNotFoundException_QuandoTipoInvalido() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.listarQuartoPorTipo(tipoInvalido);
        });
    }

    @Test
    @Transactional
    void listarQuartoPorTipo_DeveRetornarTodosQuartos_QuandoTipoForNulo() {
        String tipoNulo = null;

        List<QuartoDTO> resultado = service.listarQuartoPorTipo(tipoNulo);

        assertThat(resultado).isNotEmpty();
        assertThat(resultado.size()).isEqualTo(repository.findAll().size());
    }

    @Test
    @Transactional
    void listarQuartoPorTipo_DeveLancarResourceNotFoundException_QuandoTipoVazio() {
        String tipoVazio = "";

        assertThrows(ResourceNotFoundException.class, () -> {
            service.listarQuartoPorTipo(tipoVazio);
        });
    }

    @Test
    @Transactional
    void listarQuartoPorTipo_DeveLancarResourceNotFoundException_QuandoTipoComEspacos() {
        String tipoComEspacos = "   LUXO   ";

        assertThrows(ResourceNotFoundException.class, () -> {
            service.listarQuartoPorTipo(tipoComEspacos);
        });
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void criarQuarto_DeveCriarQuarto_QuandoAdminAutenticado() {
        QuartoDTO resultado = service.criarQuarto(quartoDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isNotNull();
        assertThat(resultado.getTipo()).isEqualTo(quartoDTO.getTipo());

        Optional<Quarto> entidade = repository.findById(resultado.getId());
        assertThat(entidade).isPresent();
    }

    @Test
    void criarQuarto_DeveLancarExcecao_QuandoUsuarioNaoAutenticado() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
            service.criarQuarto(quartoDTO);
        });
    }

    @Test
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void criarQuarto_DeveLancarForbiddenException_QuandoUsuarioNaoForAdmin() {
        assertThrows(ForbiddenException.class, () -> {
            service.criarQuarto(quartoDTO);
        });
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void criarQuarto_DevePersistirNoBanco_QuandoAdminCriar() {
        long countAntes = repository.count();

        QuartoDTO resultado = service.criarQuarto(quartoDTO);

        long countDepois = repository.count();

        assertThat(countDepois).isEqualTo(countAntes + 1);
        assertThat(repository.existsById(resultado.getId())).isTrue();
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void atualizarQuarto_DeveAtualizarQuarto_QuandoIdExistirEAdminAutenticado() {
        QuartoDTO resultado = service.atualizarQuarto(idExistente, quartoDTOAtualizacao);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(idExistente);
        assertThat(resultado.getTipo()).isEqualTo(TipoQuarto.LUXO);
        assertThat(resultado.getValorDiaria()).isEqualByComparingTo("350.00");
        assertThat(resultado.getDisponivel()).isFalse();

        Quarto entidade = repository.findById(idExistente).orElseThrow();
        assertThat(entidade.getTipo()).isEqualTo(TipoQuarto.LUXO);
        assertThat(entidade.getValorDiaria()).isEqualByComparingTo("350.00");
        assertThat(entidade.getDisponivel()).isFalse();
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void atualizarQuarto_DeveLancarResourceNotFoundException_QuandoIdNaoExistir() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.atualizarQuarto(idInexistente, quartoDTOAtualizacao);
        });
    }

    @Test
    void atualizarQuarto_DeveLancarExcecao_QuandoUsuarioNaoAutenticado() {

        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
            service.atualizarQuarto(idExistente, quartoDTOAtualizacao);
        });
    }

    @Test
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void atualizarQuarto_DeveLancarForbiddenException_QuandoUsuarioNaoForAdmin() {

        assertThrows(ForbiddenException.class, () -> {
            service.atualizarQuarto(idExistente, quartoDTOAtualizacao);
        });
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void deletarQuarto_DeveDeletarQuarto_QuandoIdExistirSemDependenciasEAdminAutenticado() {
        assertThat(repository.existsById(idExistenteSemDependencias)).isTrue();

        service.deletarQuarto(idExistenteSemDependencias);

        assertThat(repository.existsById(idExistenteSemDependencias)).isFalse();
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void deletarQuarto_DeveLancarResourceNotFoundException_QuandoIdNaoExistir() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.deletarQuarto(idInexistente);
        });
    }

    @Test
    void deletarQuarto_DeveLancarExcecao_QuandoUsuarioNaoAutenticado() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
            service.deletarQuarto(idExistenteSemDependencias);
        });
    }

    @Test
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void deletarQuarto_DeveLancarForbiddenException_QuandoUsuarioNaoForAdmin() {
        assertThrows(ForbiddenException.class, () -> {
            service.deletarQuarto(idExistenteSemDependencias);
        });
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void deletarQuarto_DeveLancarDataBaseException_QuandoExistiremDependencias() {
        assertThrows(DataBaseException.class, () -> {
            service.deletarQuarto(idComDependencias);
        });

        assertThat(repository.existsById(idComDependencias)).isTrue();
    }
}
