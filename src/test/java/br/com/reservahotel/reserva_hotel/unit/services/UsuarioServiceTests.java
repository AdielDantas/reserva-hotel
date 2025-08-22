package br.com.reservahotel.reserva_hotel.unit.services;

import br.com.reservahotel.reserva_hotel.exceptions.DataBaseException;
import br.com.reservahotel.reserva_hotel.exceptions.ForbiddenException;
import br.com.reservahotel.reserva_hotel.exceptions.ResourceNotFoundException;
import br.com.reservahotel.reserva_hotel.factory.UsuarioFactory;
import br.com.reservahotel.reserva_hotel.model.dto.NovoUsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioMinDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Usuario;
import br.com.reservahotel.reserva_hotel.model.mappers.NovoUsuarioMapper;
import br.com.reservahotel.reserva_hotel.model.mappers.UsuarioMapper;
import br.com.reservahotel.reserva_hotel.model.mappers.UsuarioMinMapper;
import br.com.reservahotel.reserva_hotel.projections.UserDetailsProjection;
import br.com.reservahotel.reserva_hotel.repositories.UsuarioRepository;
import br.com.reservahotel.reserva_hotel.services.AuthService;
import br.com.reservahotel.reserva_hotel.services.UsuarioService;
import br.com.reservahotel.reserva_hotel.util.CustomUsuario;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UsuarioServiceTests {

    @InjectMocks
    private UsuarioService service;

    @Mock
    private UsuarioRepository repository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private UsuarioMinMapper usuarioMinMapper;

    @Mock
    private NovoUsuarioMapper novoUsuarioMapper;

    @Mock
    private AuthService authService;

    @Mock
    private CustomUsuario customUsuario;

    private Long idExistente;
    private Long idInexistente;
    private Long idDependente;
    private String emailExistente;
    private String emailInexistente;
    private Usuario usuarioAdmin;
    private Usuario usuarioCliente;
    private UsuarioDTO usuarioAdminDTO;
    private UsuarioDTO usuarioClienteDTO;
    private UsuarioMinDTO usuarioMinDTO;
    private NovoUsuarioDTO novoUsuarioDTO;
    private PageImpl<Usuario> page;

    @BeforeEach
    void setUp() throws Exception {

        idExistente = 1L;
        idInexistente = 1000L;
        idDependente = 3L;
        emailExistente = "cliente@gmail.com";
        emailInexistente = "inexistente@gmail.com";
        usuarioAdmin = UsuarioFactory.criarUsuarioAdmin();
        usuarioCliente = UsuarioFactory.criarUsuarioCliente();
        usuarioAdminDTO = UsuarioFactory.criarUsuarioAdminDTO();
        usuarioClienteDTO = UsuarioFactory.criarUsuarioClienteDTO();
        usuarioMinDTO = UsuarioFactory.criarUsuarioMinDTO();
        novoUsuarioDTO = UsuarioFactory.novoUsuarioDTO();
        page = new PageImpl<>(List.of(usuarioCliente));

        when(repository.buscarUsuarioPorIdComReservas(idExistente)).thenReturn(Optional.of(usuarioCliente));
        when(repository.buscarUsuarioPorIdComReservas(idInexistente)).thenReturn(Optional.empty());

        when(repository.findByEmailIgnoreCase(emailExistente)).thenReturn(Optional.of(usuarioCliente));
        when(repository.findByEmailIgnoreCase(emailInexistente)).thenReturn(Optional.empty());

        when(repository.findAll((Pageable) any())).thenReturn(page);

        when(repository.save(any())).thenReturn(usuarioCliente);

        when(repository.getReferenceById(idExistente)).thenReturn(usuarioCliente);
        when(repository.getReferenceById(idInexistente)).thenThrow(EntityNotFoundException.class);

        when(customUsuario.usernameDoUsuarioLogado()).thenReturn("usuario_teste");

        when(usuarioMapper.toDto(usuarioCliente)).thenReturn(usuarioClienteDTO);
        when(usuarioMinMapper.toUsuarioResumoDto(usuarioCliente)).thenReturn(usuarioMinDTO);
        when(novoUsuarioMapper.toEntity(novoUsuarioDTO)).thenReturn(usuarioCliente);

        doNothing().when(novoUsuarioMapper).updateEntityFromDto(novoUsuarioDTO, usuarioCliente);
        doNothing().when(authService).validarProprioUsuarioOuAdmin(usuarioCliente.getId());
        doNothing().when(authService).validarProprioUsuarioOuAdmin(usuarioAdmin.getId());

        when(repository.existsById(idExistente)).thenReturn(true);
        when(repository.existsById(idInexistente)).thenReturn(false);
        when(repository.existsById(idDependente)).thenReturn(true);

        doNothing().when(repository).deleteById(idExistente);
        doThrow(ResourceNotFoundException.class).when(repository).deleteById(idInexistente);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(idDependente);
    }

    @Test
    void buscarUsuarioPorIdComReservas_DeveRetornarUsuario_QuandoIdExistir() {
        UsuarioDTO resultado = service.buscarUsuarioPorIdComReservas(idExistente);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(usuarioCliente.getId());
        assertThat(resultado.getEmail()).isEqualTo(usuarioCliente.getEmail());

        verify(repository, times(1)).buscarUsuarioPorIdComReservas(idExistente);
        verify(authService, times(1)).validarProprioUsuarioOuAdmin(usuarioCliente.getId());
        verify(usuarioMapper, times(1)).toDto(usuarioCliente);
    }

    @Test
    void buscarUsuarioPorIdComReservas_DeveLancarResourceNotFoundException_QuandoIdNaoExistir() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.buscarUsuarioPorIdComReservas(idInexistente);
        });

        verify(repository, times(1)).buscarUsuarioPorIdComReservas(idInexistente);
        verifyNoInteractions(usuarioMapper);
        verify(authService, never()).validarProprioUsuarioOuAdmin(any());
    }

    @Test
    void buscarUsuarioPorIdComReservas_DeveLancarForbiddenException_QuandoValidacaoPermissaoFalhar() {

        doThrow(new ForbiddenException("Acesso negado"))
                .when(authService).validarProprioUsuarioOuAdmin(usuarioCliente.getId());

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            service.buscarUsuarioPorIdComReservas(idExistente);
        });

        assertThat(exception.getMessage()).isEqualTo("Acesso negado");

        verify(repository, times(1)).buscarUsuarioPorIdComReservas(idExistente);
        verify(authService, times(1)).validarProprioUsuarioOuAdmin(usuarioCliente.getId());
        verifyNoInteractions(usuarioMapper);
    }

    @Test
    void buscarUsuarioPorEmailComReservas_DeveRetornarUsuario_QuandoIdExistir() {
        UsuarioDTO resultado = service.buscarUsuarioPorEmailComReservas(emailExistente);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(usuarioCliente.getId());
        assertThat(resultado.getEmail()).isEqualTo(usuarioCliente.getEmail());

        verify(repository, times(1)).findByEmailIgnoreCase(emailExistente);
        verify(authService, times(1)).validarProprioUsuarioOuAdmin(usuarioCliente.getId());
        verify(usuarioMapper, times(1)).toDto(usuarioCliente);
    }

    @Test
    void buscarUsuarioPorEmailComReservas_DeveLancarResourceNotFoundException_QuandoIdNaoExistir() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.buscarUsuarioPorEmailComReservas(emailInexistente);
        });

        verify(repository, times(1)).findByEmailIgnoreCase(emailInexistente);
        verifyNoInteractions(usuarioMapper);
        verify(authService, never()).validarProprioUsuarioOuAdmin(any());
    }

    @Test
    void buscarUsuarioPorEmailComReservas_DeveLancarForbiddenException_QuandoValidacaoPermissaoFalhar() {

        doThrow(new ForbiddenException("Acesso negado"))
                .when(authService).validarProprioUsuarioOuAdmin(usuarioCliente.getId());

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            service.buscarUsuarioPorEmailComReservas(emailExistente);
        });

        assertThat(exception.getMessage()).isEqualTo("Acesso negado");

        verify(repository, times(1)).findByEmailIgnoreCase(emailExistente);
        verify(authService, times(1)).validarProprioUsuarioOuAdmin(usuarioCliente.getId());
        verifyNoInteractions(usuarioMapper);
    }

    @Test
    void buscarTodosUsuariosPaginados_DeveRetornarPaginaDeUsuarios() {
        Pageable pageable = PageRequest.of(0, 5);

        Page<UsuarioMinDTO> resultado = service.buscarTodosUsuariosPaginados(pageable);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).isNotEmpty();
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).getId()).isEqualTo(usuarioMinDTO.getId());
        assertThat(resultado.getContent().get(0).getEmail()).isEqualTo(usuarioMinDTO.getEmail());

        verify(repository, times(1)).findAll(pageable);
        verify(usuarioMinMapper, times(1)).toUsuarioResumoDto(usuarioCliente);
    }

    @Test
    void salvarNovoUsuario_DeveRetornarUsuarioDTO() {
        UsuarioDTO resultado = service.salvarNovoUsuario(novoUsuarioDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(usuarioClienteDTO.getId());
        assertThat(resultado.getEmail()).isEqualTo(usuarioClienteDTO.getEmail());

        verify(novoUsuarioMapper, times(1)).toEntity(novoUsuarioDTO);
        verify(repository, times(1)).save(usuarioCliente);
        verify(usuarioMapper, times(1)).toDto(usuarioCliente);
    }

    @Test
    void atualizarUsuarioPorId_DeveRetornarUsuarioDTO_QuandoIdExistir() {
        UsuarioDTO resultado = service.atualizarUsuarioPorId(idExistente, novoUsuarioDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(usuarioClienteDTO.getId());
        assertThat(resultado.getEmail()).isEqualTo(usuarioClienteDTO.getEmail());

        verify(authService, times(1)).validarProprioUsuarioOuAdmin(idExistente);
        verify(repository, times(1)).getReferenceById(idExistente);
        verify(novoUsuarioMapper, times(1)).updateEntityFromDto(novoUsuarioDTO, usuarioCliente);
        verify(repository, times(1)).save(usuarioCliente);
        verify(usuarioMapper, times(1)).toDto(usuarioCliente);
    }

    @Test
    void atualizarUsuarioPorId_DeveLancarResourceNotFoundException_QuandoIdNaoExistir() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.atualizarUsuarioPorId(idInexistente, novoUsuarioDTO);
        });

        verify(authService, times(1)).validarProprioUsuarioOuAdmin(idInexistente);
        verify(repository, times(1)).getReferenceById(idInexistente);
        verify(novoUsuarioMapper, never()).updateEntityFromDto(any(), any());
        verify(repository, never()).save(any());
        verifyNoInteractions(usuarioMapper);
    }

    @Test
    void atualizarUsuarioPorId_DeveLancarForbiddenException_QuandoValidacaoPermissaoFalhar() {

        doThrow(new ForbiddenException("Acesso negado"))
                .when(authService).validarProprioUsuarioOuAdmin(idExistente);

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            service.atualizarUsuarioPorId(idExistente, novoUsuarioDTO);
        });

        assertThat(exception.getMessage()).isEqualTo("Acesso negado");

        verify(authService, times(1)).validarProprioUsuarioOuAdmin(idExistente);
        verify(repository, never()).getReferenceById(any());
        verifyNoInteractions(usuarioMapper);
        verifyNoInteractions(novoUsuarioMapper);
    }

    @Test
    void deletarUsuarioPorId_DeveExecutarComSucesso_QuandoIdExistir() {

        assertDoesNotThrow(() -> {
            service.deletarUsuarioPorId(idExistente);
        });
    }

    @Test
    void deletarUsuarioPorId_DeveLancarResourceNotFoundException_QuandoIdNaoExistir() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.deletarUsuarioPorId(idInexistente);
        });
    }

    @Test
    void deletarUsuarioPorId_DeveLancarDataBaseException_QuandoIdForDependente() {

        assertThrows(DataBaseException.class, () -> {
            service.deletarUsuarioPorId(idDependente);
        });
    }

    @Test
    void deletarUsuarioPorId_DeveLancarForbiddenException_QuandoValidacaoPermissaoFalhar() {

        doThrow(new ForbiddenException("Acesso negado"))
                .when(authService).validarProprioUsuarioOuAdmin(idExistente);

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            service.deletarUsuarioPorId(idExistente);
        });

        assertThat(exception.getMessage()).isEqualTo("Acesso negado");

        verify(authService, times(1)).validarProprioUsuarioOuAdmin(idExistente);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(usuarioMapper);
    }

    @Test
    void loadUserByUsername_DeveRetornarUsuarioComRoles_QuandoUsuarioExistir() {

        UserDetailsProjection proj1 = mock(UserDetailsProjection.class);
        when(proj1.getUsername()).thenReturn(emailExistente);
        when(proj1.getPassword()).thenReturn("123456");
        when(proj1.getRoleId()).thenReturn(1L);
        when(proj1.getAuthority()).thenReturn("ROLE_CLIENTE");

        UserDetailsProjection proj2 = mock(UserDetailsProjection.class);
        when(proj2.getUsername()).thenReturn(emailExistente);
        when(proj2.getPassword()).thenReturn("123456");
        when(proj2.getRoleId()).thenReturn(2L);
        when(proj2.getAuthority()).thenReturn("ROLE_ADMIN");

        when(repository.searchUserAndRolesByEmail(emailExistente))
                .thenReturn(List.of(proj1, proj2));

        UserDetails userDetails = service.loadUserByUsername(emailExistente);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(emailExistente);
        assertThat(userDetails.getPassword()).isEqualTo("123456");
        assertThat(userDetails.getAuthorities()).extracting("authority")
                .containsExactlyInAnyOrder("ROLE_CLIENTE", "ROLE_ADMIN");

        verify(repository, times(1)).searchUserAndRolesByEmail(emailExistente);
    }

    @Test
    void loadUserByUsername_DeveLancarExcecao_QuandoUsuarioNaoExistir() {
        when(repository.searchUserAndRolesByEmail(emailInexistente))
                .thenReturn(List.of());

        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername(emailInexistente);
        });

        verify(repository, times(1)).searchUserAndRolesByEmail(emailInexistente);
    }
}
