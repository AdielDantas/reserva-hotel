package br.com.reservahotel.reserva_hotel.services;

import br.com.reservahotel.reserva_hotel.exceptions.DataBaseException;
import br.com.reservahotel.reserva_hotel.exceptions.ResourceNotFoundException;
import br.com.reservahotel.reserva_hotel.model.dto.NovoUsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioMinDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Role;
import br.com.reservahotel.reserva_hotel.model.entities.Usuario;
import br.com.reservahotel.reserva_hotel.model.mappers.NovoUsuarioMapper;
import br.com.reservahotel.reserva_hotel.model.mappers.UsuarioMapper;
import br.com.reservahotel.reserva_hotel.model.mappers.UsuarioMinMapper;
import br.com.reservahotel.reserva_hotel.projections.UserDetailsProjection;
import br.com.reservahotel.reserva_hotel.repositories.RoleRepository;
import br.com.reservahotel.reserva_hotel.repositories.UsuarioRepository;
import br.com.reservahotel.reserva_hotel.util.CustomUsuario;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private UsuarioMinMapper usuarioMinMapper;

    @Autowired
    private NovoUsuarioMapper novoUsuarioMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomUsuario customUsuario;

    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public UsuarioDTO buscarUsuarioPorIdComReservas(Long id) {

        log.debug("Buscando usuário com o ID: {}", id);

        Usuario usuario = repository.buscarUsuarioPorIdComReservas(id).orElseThrow(
                () -> {
                    log.error("Usuário não encontrado com ID: {}", id);
                    return new ResourceNotFoundException("Usuário não encontrado com o ID: " + id);
                });

        authService.validarProprioUsuarioOuAdmin(usuario.getId());
        log.debug("Usuário validado: {}", usuario.getEmail());

        return usuarioMapper.toDto(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarUsuarioPorEmailComReservas(String email) {

        log.debug("Buscando usuário com o email: {}", email);

        Usuario usuario = repository.findByEmailIgnoreCase(email).orElseThrow(
                () -> {
                    log.error("Usuário não encontrado com o email: {}", email);
                    return new ResourceNotFoundException("Usuário não encontrado com o Email: " + email);
                });

        authService.validarProprioUsuarioOuAdmin(usuario.getId());
        log.info("Usuário encontrado: {}", usuario.getEmail());
        return usuarioMapper.toDto(usuario);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioMinDTO> buscarTodosUsuariosPaginados(Pageable pageable) {

        authService.validarSomenteAdmin();

        log.debug("Buscando usuários paginados - Pagina: {}, Tamanho: {}",
                pageable.getPageNumber(),
                pageable.getPageSize());

        Page<Usuario> page = repository.findAll(pageable);

        log.info("Busca de usuários concluída - Total: {}, Página atual: {}, Itens retornados: {}",
                page.getTotalElements(),
                page.getNumber(),
                page.getNumberOfElements());

        return page.map(usuarioMinMapper::toUsuarioResumoDto);
    }

    @Transactional
    public UsuarioDTO salvarNovoUsuario(NovoUsuarioDTO novoUsuarioDTO) {

        log.info("Salvando novo usuário com email: {}", novoUsuarioDTO.getEmail());

        Usuario usuario = novoUsuarioMapper.toEntity(novoUsuarioDTO);
        usuario = repository.save(usuario);

        log.debug("Usuário criado com o id: {}", usuario.getId());

        return usuarioMapper.toDto(usuario);
    }

    @Transactional
    public UsuarioDTO atualizarUsuarioPorId(Long id, NovoUsuarioDTO novoUsuarioDTO) {

        log.info("Iniciando atualização do usuário ID: {}, solicitante {}", id, customUsuario.usernameDoUsuarioLogado());

        log.debug("Validando permissões para o usuário ID: {}", id);
        authService.validarProprioUsuarioOuAdmin(id);

        try {
            Usuario usuario = repository.getReferenceById(id);
            novoUsuarioMapper.updateEntityFromDto(novoUsuarioDTO, usuario);

            log.debug("Persistindo alterações do usuário ID: {}", id);

            usuario = repository.save(usuario);

            log.info("Usuário ID: {} atualizado com sucesso. Novos dados: Nome: {}, Email: {}",
                    id,
                    usuario.getNome(),
                    usuario.getEmail());

            return usuarioMapper.toDto(usuario);
        }
        catch (EntityNotFoundException e) {

            log.error("Erro ao atualizar usuário ID: {}. Motivo: Usuário não localizado", id);
            throw new ResourceNotFoundException("Usuário não encontrado com o ID: " + id);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deletarUsuarioPorId(Long id) {

        log.info("Iniciando exclusão do usuário ID: {} (Solicitante: {})", id, customUsuario.usernameDoUsuarioLogado());

        log.debug("Validando permissões para o usuário ID: {}", id);
        authService.validarProprioUsuarioOuAdmin(id);

        if (!repository.existsById(id)) {
            log.error("Falha ao excluir usuário ID: {}. Motivo: usuário não encontrado", id);
            throw new ResourceNotFoundException("Usuário não encontrado com o ID: " + id);
        }

        try {
            log.debug("Iniciando exclusão do usuário ID: {}", id);
            repository.deleteById(id);

            repository.flush();

            log.info("Usuário ID: {} excluído com sucesso", id);
        }
        catch (DataIntegrityViolationException | PersistenceException e) {
            log.error("Falha ao excluir usuário ID: {}. Motivo: Violação da integridade referencial", id, e);
            throw new DataBaseException("Falha de integridade referencial");
        }
        catch (Exception e) {
            log.error("Erro inesperado ao excluir usuário ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("Autenticação solicitada para o email: {}", username);

        List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);

        if (result.isEmpty()) {

            log.warn("Falha na autenticação - Usuário não encontrado com o email: {}", username);
            throw new UsernameNotFoundException("Email não localizado: " + username);
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(result.get(0).getUsername());
        usuario.setSenha(result.get(0).getPassword());

        for (UserDetailsProjection projection : result) {
            usuario.addRole(new Role(
                    projection.getRoleId(),
                    projection.getAuthority()
            ));
        }

        log.info("Usuário autenticado com sucesso - Email: {}", usuario.getEmail());
        return usuario;
    }

    protected Usuario usuarioLogado() {
        String username = customUsuario.usernameDoUsuarioLogado();
        return repository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Email não localizado: " + username));
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obterMeusDados() {
        Usuario usuario = usuarioLogado();
        return usuarioMapper.toDto(usuario);
    }
}
