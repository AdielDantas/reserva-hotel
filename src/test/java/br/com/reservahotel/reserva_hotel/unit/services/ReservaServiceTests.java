package br.com.reservahotel.reserva_hotel.unit.services;

import br.com.reservahotel.reserva_hotel.exceptions.DataBaseException;
import br.com.reservahotel.reserva_hotel.exceptions.ForbiddenException;
import br.com.reservahotel.reserva_hotel.exceptions.ResourceNotFoundException;
import br.com.reservahotel.reserva_hotel.factory.QuartoFactory;
import br.com.reservahotel.reserva_hotel.factory.ReservaFactory;
import br.com.reservahotel.reserva_hotel.model.dto.ReservaDTO;
import br.com.reservahotel.reserva_hotel.model.dto.ReservaMinDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Quarto;
import br.com.reservahotel.reserva_hotel.model.entities.Reserva;
import br.com.reservahotel.reserva_hotel.model.mappers.ReservaMapper;
import br.com.reservahotel.reserva_hotel.model.mappers.ReservaMinMapper;
import br.com.reservahotel.reserva_hotel.repositories.QuartoRepository;
import br.com.reservahotel.reserva_hotel.repositories.ReservaRepository;
import br.com.reservahotel.reserva_hotel.services.AuthService;
import br.com.reservahotel.reserva_hotel.services.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class ReservaServiceTests {

    @InjectMocks
    private ReservaService service;

    @Mock
    private ReservaRepository repository;

    @Mock
    private AuthService authService;

    @Mock
    private ReservaMapper reservaMapper;

    @Mock
    private QuartoRepository quartoRepository;

    @Mock
    private ReservaMinMapper reservaMinMapper;

    private Long idExistente;
    private Long idInexistente;
    private Long idDependente;
    private Reserva reserva;
    private ReservaDTO reservaDTO;
    private ReservaMinDTO reservaMinDTO;
    private Long usuarioId;
    private String email;
    private Quarto quarto;

    @BeforeEach
    void setUp() {

        idExistente = 1L;
        idInexistente = 1000L;
        idDependente = 3L;
        reserva = ReservaFactory.criarReserva();
        reservaDTO = ReservaFactory.criarReservaDTO();
        reservaMinDTO = ReservaFactory.criarReservaMinDTO();
        usuarioId = 2L;
        email = "cliente@gmail.com";
        quarto = QuartoFactory.criarQuarto();

        when(repository.findById(idExistente)).thenReturn(Optional.of(reserva));
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        when(repository.save(reserva)).thenReturn(reserva);

        when(quartoRepository.findById(reservaDTO.getQuarto().getId())).thenReturn(Optional.of(quarto));

        when(repository.findById(reservaDTO.getQuarto().getId())).thenReturn(Optional.of(reserva));

        when(repository.existsById(idExistente)).thenReturn(true);
        when(repository.existsById(idInexistente)).thenReturn(false);
        when(repository.existsById(idDependente)).thenReturn(true);

        doNothing().when(repository).deleteById(idExistente);
        doThrow(ResourceNotFoundException.class).when(repository).deleteById(idInexistente);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(idDependente);

        when(reservaMapper.toDto(reserva)).thenReturn(reservaDTO);
        when(reservaMapper.toEntity(reservaDTO)).thenReturn(reserva);

        when(authService.resolveUsuarioId(usuarioId, email)).thenReturn(usuarioId);
        doNothing().when(authService).validarProprioUsuarioOuAdmin(reserva.getUsuario().getId());
    }

    @Test
    void buscarReservaPorId_DeveRetornarReservaDTO_QuandoIdExistir() {
        ReservaDTO resultado = service.buscarReservaPorId(idExistente);

        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(reservaDTO);
        assertThat(resultado.getId()).isEqualTo(reserva.getId());
        assertThat(resultado.getUsuario().getId()).isEqualTo(reserva.getUsuario().getId());

        verify(repository, times(1)).findById(idExistente);
        verify(authService, times(1)).validarProprioUsuarioOuAdmin(reserva.getUsuario().getId());
        verify(reservaMapper, times(1)).toDto(reserva);
    }

    @Test
    void buscarReservaPorId_DeveRetornarResourceNotFoundException_QuandoIdNaoExistir() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.buscarReservaPorId(idInexistente);
        });

        verify(repository, times(1)).findById(idInexistente);
        verifyNoInteractions(reservaMapper);
        verify(authService, never()).validarProprioUsuarioOuAdmin(any());
    }

    @Test
    void buscarReservaPorId_DeveLancarForbiddenException_QuandoValidacaoPermissaoFalhar() {

        doThrow(ForbiddenException.class).when(authService).validarProprioUsuarioOuAdmin(reserva.getUsuario().getId());

        assertThrows(ForbiddenException.class, () -> {
            service.buscarReservaPorId(idExistente);
        });

        verify(repository, times(1)).findById(idExistente);
        verify(authService, times(1)).validarProprioUsuarioOuAdmin(reserva.getUsuario().getId());
        verifyNoInteractions(reservaMapper);
    }

    @Test
    void buscarReservasPorUsuario_DeveRetornarListaReservaDTO_QuandoUsuarioTemReservas() {
        when(repository.findByUsuarioId(usuarioId)).thenReturn(List.of(reserva));

        List<ReservaDTO> resultado = service.buscarReservasPorUsuario(usuarioId, email);

        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0)).isEqualTo(reservaDTO);

        verify(authService, times(1)).resolveUsuarioId(usuarioId, email);
        verify(authService, times(1)).validarProprioUsuarioOuAdmin(usuarioId);
        verify(repository, times(1)).findByUsuarioId(usuarioId);
        verify(reservaMapper, times(1)).toDto(reserva);
    }

    @Test
    void buscarReservasPorUsuario_DeveLancarResourceNotFoundException_QuandoUsuarioNaoTemReservas() {
        when(repository.findByUsuarioId(usuarioId)).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.buscarReservasPorUsuario(usuarioId, email);
        });

        verify(authService, times(1)).resolveUsuarioId(usuarioId, email);
        verify(authService, times(1)).validarProprioUsuarioOuAdmin(usuarioId);
        verify(repository, times(1)).findByUsuarioId(usuarioId);
        verifyNoInteractions(reservaMinMapper);
    }

    @Test
    void buscarReservasPorUsuario_DeveLancarForbiddenException_QuandoValidacaoPermissaoFalhar() {
        doThrow(ForbiddenException.class).when(authService).validarProprioUsuarioOuAdmin(usuarioId);

        assertThrows(ForbiddenException.class, () -> {
            service.buscarReservasPorUsuario(usuarioId, email);
        });

        verify(authService, times(1)).resolveUsuarioId(usuarioId, email);
        verify(authService, times(1)).validarProprioUsuarioOuAdmin(usuarioId);
        verify(repository, never()).findByUsuarioId(any());
        verifyNoInteractions(reservaMapper);
    }

    @Test
    void criarReserva_DeveRetornarReservaDTO_QuandoQuartoDisponivel() {

        ReservaDTO resultado = service.criarReserva(reservaDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(reservaDTO);
        assertThat(quarto.getDisponivel()).isFalse();

        verify(quartoRepository, times(1)).findById(reservaDTO.getQuarto().getId());
        verify(reservaMapper, times(1)).toEntity(reservaDTO);
        verify(repository, times(1)).save(reserva);
        verify(reservaMapper, times(1)).toDto(reserva);
    }

    @Test
    void criarReserva_DeveLancarResourceNotFoundException_QuandoQuartoNaoExistir() {

        when(quartoRepository.findById(reservaDTO.getQuarto().getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.criarReserva(reservaDTO);
        });

        verify(quartoRepository, times(1)).findById(reservaDTO.getQuarto().getId());
        verifyNoInteractions(repository);
        verifyNoInteractions(reservaMapper);
    }

    @Test
    void criarReserva_DeveLancarIllegalStateException_QuandoQuartoNaoDisponivel() {

        quarto.setDisponivel(false);

        assertThrows(IllegalStateException.class, () -> {
            service.criarReserva(reservaDTO);
        });

        verify(quartoRepository, times(1)).findById(reservaDTO.getQuarto().getId());
        verifyNoInteractions(repository);
        verifyNoInteractions(reservaMapper);

    }

    @Test
    void atualizarReserva_DeveRetornarReservaDTO_QuandoAtualizacaoBemSucedida() {

        Quarto novoQuarto = QuartoFactory.criarQuarto();
        novoQuarto.setId(2L);
        novoQuarto.setDisponivel(true);

        when(repository.findById(idExistente)).thenReturn(Optional.of(reserva));
        when(quartoRepository.findById(novoQuarto.getId())).thenReturn(Optional.of(novoQuarto));
        when(repository.save(reserva)).thenReturn(reserva);

        // Alterando o DTO para simular troca de quarto
        reservaDTO.getQuarto().setId(novoQuarto.getId());

        ReservaDTO resultado = service.atualizarReserva(idExistente, reservaDTO);

        assertThat(resultado).isEqualTo(reservaDTO);
        assertThat(reserva.getQuarto().getId()).isEqualTo(novoQuarto.getId());
        assertThat(novoQuarto.getDisponivel()).isFalse();
        assertThat(quarto.getDisponivel()).isTrue();

        verify(authService, times(1)).validarProprioUsuarioOuAdmin(reserva.getUsuario().getId());
        verify(repository, times(1)).findById(idExistente);
        verify(reservaMapper, times(1)).updateEntityFromDto(reservaDTO, reserva);
        verify(repository, times(1)).save(reserva);
        verify(quartoRepository, times(1)).save(quarto);
        verify(quartoRepository, times(1)).save(novoQuarto);
        verify(reservaMapper, times(1)).toDto(reserva);
    }

    @Test
    void atualizarReserva_DeveLancarResourceNotFoundException_QuandoReservaNaoExistir() {

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.atualizarReserva(idInexistente, reservaDTO);
        });

        // authService NÃO deve ser chamado
        verify(repository, times(1)).findById(idInexistente);
        verifyNoInteractions(authService);
        verifyNoInteractions(reservaMapper);
        verifyNoInteractions(quartoRepository);
    }

    @Test
    void atualizarReserva_DeveLancarIllegalStateException_QuandoNovoQuartoNaoDisponivel() {

        Quarto novoQuarto = QuartoFactory.criarQuarto();
        novoQuarto.setId(2L);
        novoQuarto.setDisponivel(false);

        when(repository.findById(idExistente)).thenReturn(Optional.of(reserva));
        when(quartoRepository.findById(novoQuarto.getId())).thenReturn(Optional.of(novoQuarto));

        reservaDTO.getQuarto().setId(novoQuarto.getId());

        assertThrows(IllegalStateException.class, () -> {
            service.atualizarReserva(idExistente, reservaDTO);
        });

        verify(authService, times(1)).validarProprioUsuarioOuAdmin(reserva.getUsuario().getId());
        verify(repository, times(1)).findById(idExistente);
        verify(quartoRepository, times(1)).findById(novoQuarto.getId());
        verifyNoInteractions(reservaMapper);
        verify(quartoRepository, never()).save(any());
        verify(repository, never()).save(any());
    }

    @Test
    void atualizarReserva_DeveLancarForbiddenException_QuandoValidacaoPermissaoFalhar() {

        when(repository.findById(idExistente)).thenReturn(Optional.of(reserva));
        doThrow(ForbiddenException.class).when(authService).validarProprioUsuarioOuAdmin(reserva.getUsuario().getId());

        assertThrows(ForbiddenException.class, () -> {
            service.atualizarReserva(idExistente, reservaDTO);
        });

        verify(authService, times(1)).validarProprioUsuarioOuAdmin(reserva.getUsuario().getId());
        verify(repository, times(1)).findById(idExistente);
        verifyNoInteractions(reservaMapper);
        verifyNoInteractions(quartoRepository);
    }


    @Test
    void deletarReservaPorId_DeveExecutarComSucesso_QuandoIdExistir() {

        assertDoesNotThrow(() -> {
            service.deletarReservaPorId(idExistente);
        });

        verify(repository, times(1)).existsById(idExistente);
        verify(repository, times(1)).deleteById(idExistente);
    }

    @Test
    void deletarReservaPorId_DeveLancarResourceNotFoundException_QuandoIdNaoExistir() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.deletarReservaPorId(idInexistente);
        });
    }

    @Test
    void deletarReservaPorId_DeveLancarDataBaseException_QuandoIdForDependente() {

        assertThrows(DataBaseException.class, () -> {
            service.deletarReservaPorId(idDependente);
        });
    }

    @Test
    void deletarReservaPorId_DeveLancarForbiddenException_QuandoValidacaoPermissaoFalhar() {

        doThrow(new ForbiddenException("Acesso negado")).when(authService).validarProprioUsuarioOuAdmin(idExistente);

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            service.deletarReservaPorId(idExistente);
        });

        assertThat(exception.getMessage()).isEqualTo("Acesso negado");

        verify(authService, times(1)).validarProprioUsuarioOuAdmin(idExistente);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(reservaMapper);
    }
}