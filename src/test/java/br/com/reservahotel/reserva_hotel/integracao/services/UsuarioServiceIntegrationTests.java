package br.com.reservahotel.reserva_hotel.integracao.services;

import br.com.reservahotel.reserva_hotel.exceptions.DataBaseException;
import br.com.reservahotel.reserva_hotel.exceptions.ForbiddenException;
import br.com.reservahotel.reserva_hotel.exceptions.ResourceNotFoundException;
import br.com.reservahotel.reserva_hotel.factory.UsuarioFactory;
import br.com.reservahotel.reserva_hotel.model.dto.NovoUsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioMinDTO;
import br.com.reservahotel.reserva_hotel.model.mappers.UsuarioMapper;
import br.com.reservahotel.reserva_hotel.repositories.UsuarioRepository;
import br.com.reservahotel.reserva_hotel.services.AuthService;
import br.com.reservahotel.reserva_hotel.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UsuarioServiceIntegrationTests {

    @Autowired
    private UsuarioService service;

    @Autowired
    private UsuarioRepository repository;

    private Long idExistente;
    private Long idInexistente;
    private Long idDependente;
    private String emailExistente;
    private String emailInexistente;

    @BeforeEach
    void setUp() {

        idExistente = 2L;
        idInexistente = 1000L;
        idDependente = 3L;
        emailExistente = "cliente@gmail.com";
        emailInexistente = "inexistente@gmail.com";
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void buscarUsuarioPorIdComReservas_DeveRetornarUsuarioDTO_QuandoIdExistir() {
        UsuarioDTO resultado = service.buscarUsuarioPorIdComReservas(idExistente);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(idExistente);
        assertThat(resultado.getEmail()).isEqualTo("cliente@gmail.com");
        assertThat(resultado.getReservas()).isNotEmpty();
    }

    @Test
    @Transactional
    void buscarUsuarioPorIdComReservas_DeveLancarResourceNotFoundException_QuandoIdNaoExistirbuscarUsuarioPorIdComReservas_DeveLancarResourceNotFoundException_QuandoIdNaoExistir() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.buscarUsuarioPorIdComReservas(idInexistente);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void admin_DeveBuscarUsuarioPorIdComReservas_DeQualquerUsuario() {
        UsuarioDTO resultado = service.buscarUsuarioPorIdComReservas(3L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(3L);
        assertThat(resultado.getEmail()).isEqualTo("joao@gmail.com");
        assertThat(resultado.getReservas()).isNotEmpty();
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void buscarUsuarioPorEmailComReservas_DeveRetornarUsuarioDTO_QuandoIdExistir() {
        UsuarioDTO resultado = service.buscarUsuarioPorEmailComReservas(emailExistente);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(idExistente);
        assertThat(resultado.getEmail()).isEqualTo("cliente@gmail.com");
        assertThat(resultado.getReservas()).isNotEmpty();
    }

    @Test
    @Transactional
    void buscarUsuarioPorEmailComReservas_DeveLancarResourceNotFoundException_QuandoEmailNaoExistir() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.buscarUsuarioPorEmailComReservas(emailInexistente);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void admin_DeveBuscarUsuarioPorEmailComReservas_DeQualquerUsuario() {
        UsuarioDTO resultado = service.buscarUsuarioPorEmailComReservas("joao@gmail.com");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(3L);
        assertThat(resultado.getEmail()).isEqualTo("joao@gmail.com");
        assertThat(resultado.getReservas()).isNotEmpty();
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void buscarTodosUsuariosPaginados_DeveRetornarPagina_QuandoUsuarioForAdmin() {
        PageRequest pageable = PageRequest.of(0, 5);

        Page<UsuarioMinDTO> resultado = service.buscarTodosUsuariosPaginados(pageable);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).isNotEmpty();
        assertThat(resultado.getContent().size()).isLessThanOrEqualTo(5);
        assertThat(resultado.getTotalElements()).isGreaterThan(0);

        UsuarioMinDTO usuario = resultado.getContent().get(0);
        assertThat(usuario.getId()).isNotNull();
        assertThat(usuario.getEmail()).isNotBlank();
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void buscarTodosUsuariosPaginados_DeveLancarExcecao_QuandoUsuarioNaoForAdmin() {
        PageRequest pageable = PageRequest.of(0, 5);

        assertThrows(ForbiddenException.class, () -> {
            service.buscarTodosUsuariosPaginados(pageable);
        });
    }

    @Test
    @Transactional
    void salvarNovoUsuario_DeveRetornarUsuarioDTO() {
        NovoUsuarioDTO dto = UsuarioFactory.novoUsuarioDTO();

        UsuarioDTO resultado = service.salvarNovoUsuario(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(7L);
        assertThat(resultado.getNome()).isEqualTo(dto.getNome());
        assertThat(resultado.getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void atualizarUsuarioPorId_DeveRetornarUsuarioAtualizado_QuandoIdExistirEUsuarioForAdmin() {
        NovoUsuarioDTO dto = UsuarioFactory.novoUsuarioDTO();

        UsuarioDTO resultado = service.atualizarUsuarioPorId(idExistente, dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(idExistente);
        assertThat(resultado.getNome()).isEqualTo(dto.getNome());
        assertThat(resultado.getEmail()).isEqualTo(dto.getEmail());

        assertThat(repository.findById(idExistente).get().getEmail())
                .isEqualTo(dto.getEmail());
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void atualizarUsuarioPorId_DeveLancarResourceNotFoundException_QuandoIdNaoExistir() {
        NovoUsuarioDTO dto = UsuarioFactory.novoUsuarioDTO();

        assertThrows(ResourceNotFoundException.class, () -> {
            service.atualizarUsuarioPorId(idInexistente, dto);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void atualizarUsuarioPorId_DeveLancarForbiddenException_QuandoUsuarioNaoForProprioOuAdmin() {
        NovoUsuarioDTO dto = UsuarioFactory.novoUsuarioDTO();

        assertThrows(ForbiddenException.class, () -> {
            service.atualizarUsuarioPorId(1L, dto);
        });
    }

    @Test
    @Transactional
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void atualizarUsuarioPorId_DeveAtualizar_QuandoUsuarioAtualizaProprioPerfil() {
        NovoUsuarioDTO dto = UsuarioFactory.novoUsuarioDTO();

        UsuarioDTO resultado = service.atualizarUsuarioPorId(idExistente, dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(idExistente);
        assertThat(resultado.getNome()).isEqualTo(dto.getNome());
        assertThat(resultado.getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void deletarUsuarioPorId_DeveExcluirUsuario_QuandoIdExistirEUsuarioNaoTiverReservas() {
        Long idParaExcluir = 6L; // Usuario Sem Reserva

        service.deletarUsuarioPorId(idParaExcluir);

        assertThat(repository.existsById(idParaExcluir)).isFalse();
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void deletarUsuarioPorId_DeveLancarResourceNotFoundException_QuandoIdNaoExistir() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.deletarUsuarioPorId(idInexistente);
        });
    }

    @Test
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void deletarUsuarioPorId_DeveLancarDataBaseException_QuandoClienteTentaExcluirProprioUsuarioComReservas() {
        Long idExistente = 2L;

        assertThrows(DataBaseException.class, () -> {
            service.deletarUsuarioPorId(idExistente);
        });

        // Verifique que o usuário ainda existe (não foi deletado devido à exceção)
        assertTrue(repository.existsById(idExistente));
    }

    @Test
    @WithMockUser(username = "cliente@gmail.com", roles = {"CLIENTE"})
    void deletarUsuarioPorId_DeveLancarForbiddenException_QuandoClienteTentaExcluirOutroUsuario() {
        assertThrows(ForbiddenException.class, () -> {
            service.deletarUsuarioPorId(1L); // Admin
        });
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void deletarUsuarioPorId_DeveLancarDataBaseException_QuandoUsuarioPossuirReservas() {
        Long idComReservas = 3L;

        assertThrows(DataBaseException.class, () -> {
            service.deletarUsuarioPorId(idComReservas);
        });

        // Verifique que o usuário ainda existe
        assertTrue(repository.existsById(idComReservas));
    }

    @Test
    void loadUserByUsername_DeveRetornarUserDetails_QuandoEmailExistir() {
        UserDetails userDetails = service.loadUserByUsername("cliente@gmail.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("cliente@gmail.com");
        assertThat(userDetails.getAuthorities()).isNotEmpty();
    }

    @Test
    void loadUserByUsername_DeveLancarUsernameNotFoundException_QuandoEmailNaoExistir() {
        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername("emailinexistente@gmail.com");
        });
    }
}
