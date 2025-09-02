package br.com.reservahotel.reserva_hotel.integracao.services;

import br.com.reservahotel.reserva_hotel.exceptions.DataBaseException;
import br.com.reservahotel.reserva_hotel.exceptions.ForbiddenException;
import br.com.reservahotel.reserva_hotel.exceptions.ResourceNotFoundException;
import br.com.reservahotel.reserva_hotel.factory.ReservaFactory;
import br.com.reservahotel.reserva_hotel.model.dto.ReservaDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Quarto;
import br.com.reservahotel.reserva_hotel.model.entities.Reserva;
import br.com.reservahotel.reserva_hotel.model.enums.StatusReserva;
import br.com.reservahotel.reserva_hotel.model.mappers.ReservaMapper;
import br.com.reservahotel.reserva_hotel.repositories.QuartoRepository;
import br.com.reservahotel.reserva_hotel.repositories.ReservaRepository;
import br.com.reservahotel.reserva_hotel.repositories.UsuarioRepository;
import br.com.reservahotel.reserva_hotel.services.AuthService;
import br.com.reservahotel.reserva_hotel.services.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static br.com.reservahotel.reserva_hotel.model.enums.StatusReserva.CONFIRMADA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
public class ReservaServiceIntegrationTests {

    @Autowired
    private ReservaService service;

    @Autowired
    private ReservaRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReservaMapper reservaMapper;

    @Autowired
    private QuartoRepository quartoRepository;

    @Autowired
    private AuthService authService;

    private Long idExistente;
    private Long idInexistente;
    private Long idReservaOutroUsuario;
    private Long usuarioComReservasId;
    private Long usuarioSemReservasId;
    private Long usuarioIdInexistente;
    private String emailComReservas;
    private String emailSemReservas;
    private String emailInexistente;
    private Long quartoDisponivelId;
    private Long quartoOcupadoId;
    private Long quartoInexistenteId;
    private Long usuarioValidoId;
    private Long reservaParaAtualizarId;
    private Long quartoAlternativoId;
    private Long reservaDeletavelId;
    private Long reservaComDependentesId;
    private Long reservaInexistenteId;
    private Long reservaOutroUsuarioId;

    @BeforeEach
    void setUp() {
        idExistente = 1L; // Reserva do cliente@gmail.com (ID 2)
        idInexistente = 1000L;
        idReservaOutroUsuario = 2L; // Reserva da maria@gmail.com (ID 4)

        usuarioComReservasId = 2L; // Cliente Teste - tem reservas
        usuarioSemReservasId = 6L; // Usuario Sem Reserva
        usuarioIdInexistente = 1000L;
        emailComReservas = "cliente@gmail.com";
        emailSemReservas = "semreserva@gmail.com";
        emailInexistente = "inexistente@gmail.com";

        quartoDisponivelId = 2L; // Quarto ECONOMICO disponível
        quartoOcupadoId = 1L;    // Quarto LUXO ocupado
        quartoInexistenteId = 1000L;
        usuarioValidoId = 2L;    // Cliente Teste

        reservaParaAtualizarId = 1L; // Reserva para testar atualização
        quartoAlternativoId = 3L;    // Quarto STANDARD disponível para testes de mudança

        reservaDeletavelId = 3L; // Reserva PENDENTE do João Silva (pode ser deletada)
        reservaComDependentesId = 1L; // Reserva CONFIRMADA com possíveis dependentes
        reservaInexistenteId = 1000L;
        reservaOutroUsuarioId = 2L; // Reserva da Maria Souza
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void buscarReservaPorId_DeveRetornarReservaDTOComDadosCorretos_QuandoForProprioUsuario() {
        ReservaDTO resultado = service.buscarReservaPorId(idExistente);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(idExistente);
        assertThat(resultado.getUsuario().getId()).isEqualTo(2L); // ID do cliente@gmail.com
        assertThat(resultado.getUsuario().getEmail()).isEqualTo("cliente@gmail.com");
        assertThat(resultado.getValorTotal()).isEqualTo(new BigDecimal("700.00"));
        assertThat(resultado.getStatus()).isEqualTo(CONFIRMADA);
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void buscarReservaPorId_DeveRetornarReservaDTO_QuandoForAdmin() {
        ReservaDTO resultado = service.buscarReservaPorId(idExistente);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(idExistente);
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void buscarReservaPorId_DeveLancarResourceNotFoundException_QuandoIdNaoExistir() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.buscarReservaPorId(idInexistente);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void buscarReservaPorId_DeveLancarForbiddenException_QuandoReservaNaoForDoUsuario() {

        assertThrows(ForbiddenException.class, () -> {
            service.buscarReservaPorId(idReservaOutroUsuario);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "joao@gmail.com", roles = {"CLIENTE"})
    void buscarReservaPorId_DeveLancarForbiddenException_QuandoUsuarioDiferenteTentaAcessar() {

        // joao@gmail.com tentando acessar reserva do cliente@gmail.com
        assertThrows(ForbiddenException.class, () -> {
            service.buscarReservaPorId(idExistente);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void buscarReservasPorUsuario_DeveRetornarListaReservas_QuandoProprioUsuarioPorId() {
        List<ReservaDTO> resultado = service.buscarReservasPorUsuario(usuarioComReservasId, null);

        assertThat(resultado).isNotNull().isNotEmpty();
        assertThat(resultado.size()).isGreaterThan(0);
        resultado.forEach(reserva -> {
            assertThat(reserva.getId()).isNotNull();
            assertThat(reserva.getUsuario().getId()).isEqualTo(usuarioComReservasId);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void buscarReservasPorUsuario_DeveRetornarListaReservas_QuandoAdminPorId() {
        List<ReservaDTO> resultado = service.buscarReservasPorUsuario(usuarioComReservasId, null);

        assertThat(resultado).isNotNull().isNotEmpty();
        resultado.forEach(reserva -> {
            assertThat(reserva.getUsuario().getId()).isEqualTo(usuarioComReservasId);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void buscarReservasPorUsuario_DeveRetornarListaReservas_QuandoProprioUsuarioPorEmail() {
        List<ReservaDTO> resultado = service.buscarReservasPorUsuario(null, emailComReservas);

        assertThat(resultado).isNotNull().isNotEmpty();
        resultado.forEach(reserva -> {
            assertThat(reserva.getUsuario().getEmail()).isEqualTo(emailComReservas);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void buscarReservasPorUsuario_DeveRetornarListaReservas_QuandoAdminPorEmail() {
        List<ReservaDTO> resultado = service.buscarReservasPorUsuario(null, emailComReservas);

        assertThat(resultado).isNotNull().isNotEmpty();
        resultado.forEach(reserva -> {
            assertThat(reserva.getUsuario().getEmail()).isEqualTo(emailComReservas);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "semreserva@gmail.com", roles = {"CLIENTE"})
    void buscarReservasPorUsuario_DeveLancarResourceNotFoundException_QuandoUsuarioSemReservasPorId() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.buscarReservasPorUsuario(usuarioSemReservasId, null);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "semreserva@gmail.com", roles = {"CLIENTE"})
    void buscarReservasPorUsuario_DeveLancarResourceNotFoundException_QuandoUsuarioSemReservasPorEmail() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.buscarReservasPorUsuario(null, emailSemReservas);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "joao@gmail.com", roles = {"CLIENTE"})
    void buscarReservasPorUsuario_DeveLancarForbiddenException_QuandoUsuarioTentaAcessarOutroUsuarioPorId() {
        assertThrows(ForbiddenException.class, () -> {
            service.buscarReservasPorUsuario(usuarioComReservasId, null); // joao tentando acessar cliente
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "joao@gmail.com", roles = {"CLIENTE"})
    void buscarReservasPorUsuario_DeveLancarForbiddenException_QuandoUsuarioTentaAcessarOutroUsuarioPorEmail() {
        assertThrows(ForbiddenException.class, () -> {
            service.buscarReservasPorUsuario(null, emailComReservas); // joao tentando acessar cliente
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void buscarReservasPorUsuario_DeveLancarResourceNotFoundException_QuandoUsuarioIdNaoExiste() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.buscarReservasPorUsuario(usuarioIdInexistente, null);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void buscarReservasPorUsuario_DeveLancarResourceNotFoundException_QuandoEmailNaoExiste() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.buscarReservasPorUsuario(null, emailInexistente);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void buscarReservasPorUsuario_DeveRetornarReservasComDadosCorretos() {
        List<ReservaDTO> resultado = service.buscarReservasPorUsuario(usuarioComReservasId, null);

        assertThat(resultado).isNotNull().isNotEmpty();

        resultado.forEach(reserva -> {
            assertThat(reserva.getValorTotal()).isGreaterThan(new BigDecimal("0.0"));
            assertThat(reserva.getCheckin()).isBefore(reserva.getCheckout());
        });
    }

    @Test
    @Transactional
    void criarReserva_DeveRetornarReservaDTO_QuandoDadosValidos() {
        // Usando a Factory existente
        ReservaDTO reservaDTO = ReservaFactory.criarReservaDTOParaCriacao(quartoDisponivelId, usuarioValidoId);

        ReservaDTO resultado = service.criarReserva(reservaDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isNotNull();
        assertThat(resultado.getQuarto().getId()).isEqualTo(quartoDisponivelId);
        assertThat(resultado.getUsuario().getId()).isEqualTo(usuarioValidoId);
    }

    @Test
    @Transactional
    void criarReserva_DeveLancarResourceNotFoundException_QuandoQuartoNaoExiste() {
        ReservaDTO reservaDTO = ReservaFactory.criarReservaDTOParaCriacao(quartoInexistenteId, usuarioValidoId);

        assertThrows(ResourceNotFoundException.class, () -> {
            service.criarReserva(reservaDTO);
        });
    }

    @Test
    @Transactional
    void criarReserva_DeveLancarIllegalStateException_QuandoQuartoNaoDisponivel() {
        ReservaDTO reservaDTO = ReservaFactory.criarReservaDTOParaCriacao(quartoOcupadoId, usuarioValidoId);

        assertThrows(IllegalStateException.class, () -> {
            service.criarReserva(reservaDTO);
        });
    }

    @Test
    @Transactional
    void criarReserva_DeveLancarException_QuandoCheckinDepoisDoCheckout() {
        ReservaDTO reservaDTO = ReservaFactory.criarReservaDTOParaCriacao(quartoDisponivelId, usuarioValidoId);

        // Ajusta datas manualmente para o cenário específico
        reservaDTO.setCheckin(LocalDate.now().plusDays(5));
        reservaDTO.setCheckout(LocalDate.now().plusDays(3));

        assertThrows(IllegalArgumentException.class, () -> {
            service.criarReserva(reservaDTO);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void atualizarReserva_DeveRetornarReservaAtualizada_QuandoDadosValidos() {
        ReservaDTO reservaDTO = ReservaFactory.criarReservaDTO();

        ReservaDTO resultado = service.atualizarReserva(reservaParaAtualizarId, reservaDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(reservaParaAtualizarId);
        assertThat(resultado.getCheckin()).isEqualTo(reservaDTO.getCheckin());
        assertThat(resultado.getCheckout()).isEqualTo(reservaDTO.getCheckout());
        assertThat(resultado.getStatus()).isEqualTo(StatusReserva.CONFIRMADA);
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void atualizarReserva_DeveRetornarReservaAtualizada_QuandoAdmin() {
        ReservaDTO reservaDTO = ReservaFactory.criarReservaDTO();

        ReservaDTO resultado = service.atualizarReserva(reservaParaAtualizarId, reservaDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(reservaParaAtualizarId);
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void atualizarReserva_DeveLancarResourceNotFoundException_QuandoReservaNaoExiste() {
        ReservaDTO reservaDTO = ReservaFactory.criarReservaDTO();

        assertThrows(ResourceNotFoundException.class, () -> {
            service.atualizarReserva(idInexistente, reservaDTO);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "joao@gmail.com", roles = {"CLIENTE"})
    void atualizarReserva_DeveLancarForbiddenException_QuandoUsuarioNaoForDono() {
        ReservaDTO reservaDTO = ReservaFactory.criarReservaDTO();

        assertThrows(ForbiddenException.class, () -> {
            service.atualizarReserva(reservaParaAtualizarId, reservaDTO);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void atualizarReserva_DeveMudarQuarto_QuandoNovoQuartoDisponivel() {
        ReservaDTO reservaDTO = ReservaFactory.criarReservaDTO();
        reservaDTO.getQuarto().setId(quartoAlternativoId);

        ReservaDTO resultado = service.atualizarReserva(reservaParaAtualizarId, reservaDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getQuarto().getId()).isEqualTo(quartoAlternativoId);
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void atualizarReserva_DeveLancarIllegalStateException_QuandoNovoQuartoIndisponivel() {
        // quarto ocupado no seed
        Long quartoRealmenteOcupadoId = 6L;

        ReservaDTO reservaDTO = ReservaFactory.criarReservaDTO();
        reservaDTO.getQuarto().setId(quartoRealmenteOcupadoId);

        assertThrows(IllegalStateException.class, () -> {
            service.atualizarReserva(reservaParaAtualizarId, reservaDTO);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void atualizarReserva_DeveLancarResourceNotFoundException_QuandoNovoQuartoNaoExiste() {
        ReservaDTO reservaDTO = ReservaFactory.criarReservaDTO();
        reservaDTO.getQuarto().setId(quartoInexistenteId);

        assertThrows(ResourceNotFoundException.class, () -> {
            service.atualizarReserva(reservaParaAtualizarId, reservaDTO);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void atualizarReserva_DeveManterMesmoQuarto_QuandoQuartoNaoMudou() {
        Reserva reservaOriginal = repository.findById(reservaParaAtualizarId).orElseThrow();
        Long quartoOriginalId = reservaOriginal.getQuarto().getId();

        ReservaDTO reservaDTO = ReservaFactory.criarReservaDTO();
        reservaDTO.getQuarto().setId(quartoOriginalId);

        ReservaDTO resultado = service.atualizarReserva(reservaParaAtualizarId, reservaDTO);

        assertThat(resultado.getQuarto().getId()).isEqualTo(quartoOriginalId);
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void atualizarReserva_DeveValidarDatas_QuandoDatasInvalidas() {
        ReservaDTO reservaDTO = ReservaFactory.criarReservaDTO();
        reservaDTO.setCheckin(LocalDate.now().plusDays(5));
        reservaDTO.setCheckout(LocalDate.now().plusDays(3));

        assertThrows(IllegalArgumentException.class, () -> {
            service.atualizarReserva(reservaParaAtualizarId, reservaDTO);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void atualizarReserva_DeveLancarException_QuandoReservaDTONula() {
        assertThrows(RuntimeException.class, () -> {
            service.atualizarReserva(reservaParaAtualizarId, null);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "joao@gmail.com", roles = {"CLIENTE"})
    void deletarReservaPorId_DeveDeletarReserva_QuandoProprioUsuario() {
        assertTrue(repository.existsById(reservaDeletavelId));

        service.deletarReservaPorId(reservaDeletavelId);

        assertFalse(repository.existsById(reservaDeletavelId));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void deletarReservaPorId_DeveDeletarReserva_QuandoAdmin() {
        assertTrue(repository.existsById(reservaDeletavelId));

        service.deletarReservaPorId(reservaDeletavelId);

        assertFalse(repository.existsById(reservaDeletavelId));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void deletarReservaPorId_DeveLancarResourceNotFoundException_QuandoReservaNaoExiste() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.deletarReservaPorId(reservaInexistenteId);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "joao@gmail.com", roles = {"CLIENTE"})
    void deletarReservaPorId_DeveLancarForbiddenException_QuandoUsuarioNaoForDono() {
        assertThrows(ForbiddenException.class, () -> {
            service.deletarReservaPorId(reservaOutroUsuarioId);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void deletarReservaPorId_DeveLiberarQuarto_QuandoReservaDeletada() {
        Long reservaId = 1L;
        Long quartoId = 1L;

        Quarto quartoAntes = quartoRepository.findById(quartoId).orElseThrow();
        assertFalse(quartoAntes.getDisponivel(), "Quarto deveria estar ocupado pelo seed");

        service.deletarReservaPorId(reservaId);

        Quarto quartoDepois = quartoRepository.findById(quartoId).orElseThrow();
        assertTrue(quartoDepois.getDisponivel(), "Quarto deveria ser liberado após deleção");
    }

    @Test
    void deletarReservaPorId_DeveLancarAuthenticationCredentialsNotFoundException_QuandoNaoAutenticado() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
            service.deletarReservaPorId(reservaDeletavelId);
        });
    }
}
