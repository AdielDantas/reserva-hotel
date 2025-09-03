package br.com.reservahotel.reserva_hotel.unit.services;

import br.com.reservahotel.reserva_hotel.exceptions.DataBaseException;
import br.com.reservahotel.reserva_hotel.exceptions.ForbiddenException;
import br.com.reservahotel.reserva_hotel.exceptions.ResourceNotFoundException;
import br.com.reservahotel.reserva_hotel.factory.QuartoFactory;
import br.com.reservahotel.reserva_hotel.model.dto.QuartoDTO;
import br.com.reservahotel.reserva_hotel.model.dto.QuartoMinDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Quarto;
import br.com.reservahotel.reserva_hotel.model.enums.TipoQuarto;
import br.com.reservahotel.reserva_hotel.model.mappers.QuartoMapper;
import br.com.reservahotel.reserva_hotel.model.mappers.QuartoMinMapper;
import br.com.reservahotel.reserva_hotel.repositories.QuartoRepository;
import br.com.reservahotel.reserva_hotel.services.AuthService;
import br.com.reservahotel.reserva_hotel.services.QuartoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class QuartoServiceTests {

    @InjectMocks
    private QuartoService service;

    @Mock
    private QuartoRepository repository;

    @Mock
    private QuartoMapper quartoMapper;

    @Mock
    private QuartoMinMapper quartoMinMapper;

    @Mock
    private AuthService authService;

    private Long idExistente;
    private Long idInexistente;
    private Long idDependente;
    private Quarto quarto;
    private QuartoDTO quartoDTO;
    private QuartoMinDTO quartoMinDTO;

    @BeforeEach
    void setUp() throws Exception {

        idExistente = 1L;
        idInexistente = 1000L;
        idDependente = 3L;
        quarto = QuartoFactory.criarQuarto();
        quartoDTO = QuartoFactory.criarQuartoDTO();
        quartoMinDTO = QuartoFactory.criarQuartoMinDTO();

        when(repository.findById(idExistente)).thenReturn(Optional.of(quarto));
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        when(repository.findAll()).thenReturn(List.of(quarto));

        when(repository.findByTipo(TipoQuarto.ECONOMICO)).thenReturn(List.of(quarto));

        when(repository.save(quarto)).thenReturn(quarto);

        when(repository.getReferenceById(idExistente)).thenReturn(quarto);
        when(repository.getReferenceById(idInexistente)).thenThrow(EntityNotFoundException.class);

        when(repository.existsById(idExistente)).thenReturn(true);
        when(repository.existsById(idInexistente)).thenReturn(false);
        when(repository.existsById(idDependente)).thenReturn(true);

        doNothing().when(repository).deleteById(idExistente);
        doThrow(ResourceNotFoundException.class).when(repository).deleteById(idInexistente);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(idDependente);

        when(quartoMapper.toDto(quarto)).thenReturn(quartoDTO);
        when(quartoMapper.toEntity(quartoDTO)).thenReturn(quarto);
        doNothing().when(quartoMapper).updateEntityFromDto(quartoDTO, quarto);

        doNothing().when(authService).validarSomenteAdmin();
    }

    @Test
    void buscarQuartoPorId_DeveRetornarQuartoDTO_QuandoIdExistir() {

        QuartoDTO resultado = service.buscarQuartoPorId(idExistente);

        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(quartoDTO);
        assertThat(resultado.getId()).isEqualTo(quarto.getId());

        verify(repository, times(1)).findById(idExistente);
        verify(quartoMapper, times(1)).toDto(quarto);
    }

    @Test
    void buscarQuartoPorId_DeveLancarResourceNotFoundException_QuandoIdInexistir() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.buscarQuartoPorId(idInexistente);
        });

        verify(repository, times(1)).findById(idInexistente);
        verifyNoInteractions(quartoMapper);
    }

    @Test
    void ListarTodosOsQuartos_DeveRetornarListaDeQuartoDTO() {

        List<QuartoDTO> resultado = service.listarTodosOsQuartos();

        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0)).isEqualTo(quartoDTO);

        verify(repository, times(1)).findAll();
        verify(quartoMapper, times(1)).toDto(quarto);
    }

    @Test
    void listarTodosOsQuartos_DeveRetornarListaVazia_QuandoNaoExistirQuartos() {
        when(repository.findAll()).thenReturn(List.of());

        List<QuartoDTO> resultado = service.listarTodosOsQuartos();

        assertThat(resultado).isNotNull().isEmpty();

        verify(repository, times(1)).findAll();
        verifyNoInteractions(quartoMapper);
    }

    @Test
    void listarQuartosDisponiveis_DeveLancarIllegalArgumentException_QuandoDataInicialForDepoisDeDataFinal() {
        LocalDate dataInicial = LocalDate.of(2025, 1, 10);
        LocalDate dataFinal = LocalDate.of(2025, 1, 5);

        assertThrows(IllegalArgumentException.class, () -> {
            service.listarQuartosDisponiveis(dataInicial, dataFinal);
        });

        verifyNoInteractions(repository);
        verifyNoInteractions(quartoMapper);
    }

    @Test
    void listarQuartosDisponiveis_DeveRetornarListaDeQuartoDTO_QuandoDatasForemValidas() {
        LocalDate dataInicial = LocalDate.of(2035, 1, 1);
        LocalDate dataFinal = LocalDate.of(2035, 1, 10);

        when(repository.findDisponiveisPorPeriodo(dataInicial, dataFinal)).thenReturn(List.of(quarto));

        List<QuartoDTO> resultado = service.listarQuartosDisponiveis(dataInicial, dataFinal);

        assertThat(resultado).isNotNull().hasSize(1);
        assertThat(resultado.get(0)).isEqualTo(quartoDTO);

        verify(repository, times(1)).findDisponiveisPorPeriodo(dataInicial, dataFinal);
        verify(quartoMapper, times(1)).toDto(quarto);
    }

    @Test
    void listarQuartosDisponiveis_DeveRetornarListaDeQuartoDTO_QuandoNaoPassarDatas() {
        when(repository.findByDisponivelTrue()).thenReturn(List.of(quarto));

        List<QuartoDTO> resultado = service.listarQuartosDisponiveis(null, null);

        assertThat(resultado).isNotNull().hasSize(1);
        assertThat(resultado.get(0)).isEqualTo(quartoDTO);

        verify(repository, times(1)).findByDisponivelTrue();
        verify(quartoMapper, times(1)).toDto(quarto);
    }

    @Test
    void listarQuartosDisponiveis_DeveRetornarListaVazia_QuandoNaoExistiremQuartosDisponiveisSemPeriodo() {
        when(repository.findByDisponivelTrue()).thenReturn(List.of());

        List<QuartoDTO> resultado = service.listarQuartosDisponiveis(null, null);

        assertThat(resultado).isNotNull().isEmpty();

        verify(repository, times(1)).findByDisponivelTrue();
        verifyNoInteractions(quartoMapper);
    }

    @Test
    void listarQuartosDisponiveis_DeveRetornarListaVazia_QuandoNaoExistiremQuartosDisponiveisPorPeriodo() {
        LocalDate dataInicial = LocalDate.of(2035, 2, 1);
        LocalDate dataFinal = LocalDate.of(2035, 2, 5);

        when(repository.findDisponiveisPorPeriodo(dataInicial, dataFinal)).thenReturn(List.of());

        List<QuartoDTO> resultado = service.listarQuartosDisponiveis(dataInicial, dataFinal);

        assertThat(resultado).isNotNull().isEmpty();

        verify(repository, times(1)).findDisponiveisPorPeriodo(dataInicial, dataFinal);
        verifyNoInteractions(quartoMapper);
    }

    @Test
    void listarQuartoPorTipo_DeveRetornarListaDeQuartoDTO_QuandoTipoExistir() {

        List<QuartoDTO> resultado = service.listarQuartoPorTipo("ECONOMICO");

        assertThat(resultado).isNotNull().hasSize(1);
        assertThat(resultado.get(0)).isEqualTo(quartoDTO);

        verify(repository, times(1)).findByTipo(TipoQuarto.ECONOMICO);
        verify(quartoMapper, times(1)).toDto(quarto);
    }

    @Test
    void listarQuartoPorTipo_DeveRetornarListaVazia_QuandoNaoExistirQuartos() {
        when(repository.findByTipo(TipoQuarto.LUXO)).thenReturn(List.of());

        List<QuartoDTO> resultado = service.listarQuartoPorTipo("LUXO");

        assertThat(resultado).isNotNull().isEmpty();

        verify(repository, times(1)).findByTipo(TipoQuarto.LUXO);
        verifyNoInteractions(quartoMapper);
    }

    @Test
    void listarQuartoPorTipo_DeveLancarResourceNotFoundException_QuandoTipoForInvalido() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.listarQuartoPorTipo("INVALIDO");
        });

        verifyNoInteractions(repository);
        verifyNoInteractions(quartoMapper);
    }

    @Test
    void criarQuarto_DeveRetornarQuartoDTO_QuandoUsuarioForAdmin() {

        QuartoDTO resultado = service.criarQuarto(quartoDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(quartoDTO);

        verify(authService, times(1)).validarSomenteAdmin();
        verify(quartoMapper, times(1)).toEntity(quartoDTO);
        verify(repository, times(1)).save(quarto);
        verify(quartoMapper, times(1)).toDto(quarto);
    }

    @Test
    void criarQuarto_DeveLancarForbiddenException_QuandoUsuarioNaoForAdmin() {
        doThrow(new ForbiddenException("Acesso negado")).when(authService).validarSomenteAdmin();

        assertThrows(ForbiddenException.class, () -> {
            service.criarQuarto(quartoDTO);
        });

        verify(authService, times(1)).validarSomenteAdmin();
        verifyNoInteractions(repository);
        verifyNoInteractions(quartoMapper);
    }

    @Test
    void atualizarQuarto_DeveRetornarQuartoDTO_QuandoIdExistirEUsuarioForAdmin() {

        QuartoDTO resultado = service.atualizarQuarto(idExistente, quartoDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(quartoDTO);

        verify(authService, times(1)).validarSomenteAdmin();
        verify(repository, times(1)).getReferenceById(idExistente);
        verify(quartoMapper, times(1)).updateEntityFromDto(quartoDTO, quarto);
        verify(repository, times(1)).save(quarto);
        verify(quartoMapper, times(1)).toDto(quarto);
    }

    @Test
    void atualizarQuarto_DeveLancarResourceNotFoundException_QuandoIdNaoExistir() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.atualizarQuarto(idInexistente, quartoDTO);
        });

        verify(authService, times(1)).validarSomenteAdmin();
        verify(repository, times(1)).getReferenceById(idInexistente);
        verify(repository, never()).save(any());
        verify(quartoMapper, never()).toDto(any());
    }

    @Test
    void atualizarQuarto_DeveLancarForbiddenException_QuandoUsuarioNaoForAdmin() {

        doThrow(new ForbiddenException("Acesso negado")).when(authService).validarSomenteAdmin();

        assertThrows(ForbiddenException.class, () -> {
            service.atualizarQuarto(idExistente, quartoDTO);
        });

        verify(authService, times(1)).validarSomenteAdmin();
        verify(repository, never()).getReferenceById(any());
        verify(repository, never()).save(any());
        verifyNoInteractions(quartoMapper);
    }

    @Test
    void deletarQuarto_DeveExecutarComSucesso_QuandoIdExistir() {

        assertDoesNotThrow(() -> {
            service.deletarQuarto(idExistente);
        });

        verify(authService, times(1)).validarSomenteAdmin();
        verify(repository, times(1)).deleteById(idExistente);
    }

    @Test
    void deletarQuarto_DeveLancarResourceNotFoundException_QuandoIdNaoExistir() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.deletarQuarto(idInexistente);
        });

        verify(authService, times(1)).validarSomenteAdmin();
    }

    @Test
    void deletarQuarto_DeveLancarDataBaseException_QuandoIdForDependente() {

        assertThrows(DataBaseException.class, () -> {
            service.deletarQuarto(idDependente);
        });

        verify(authService, times(1)).validarSomenteAdmin();
    }
}
