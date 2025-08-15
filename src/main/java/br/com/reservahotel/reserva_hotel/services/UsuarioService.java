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
        Usuario usuario = repository.buscarUsuarioPorIdComReservas(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        authService.validarProprioUsuarioOuAdmin(usuario.getId());
        return usuarioMapper.toDto(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarUsuarioPorEmailComReservas(String email) {
        Usuario usuario = repository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new ResourceNotFoundException("Usuário não encontrado com o Email: " + email));
        authService.validarProprioUsuarioOuAdmin(usuario.getId());
        return usuarioMapper.toDto(usuario);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioMinDTO> buscarTodosUsuarosPaginados(Pageable pageable) {
        Page<Usuario> page = repository.findAll(pageable);
        return page.map(usuarioMinMapper::toUsuarioResumoDto);
    }

    @Transactional
    public UsuarioDTO salvarNovoUsuario(NovoUsuarioDTO novoUsuarioDTO) {
        Usuario usuario = novoUsuarioMapper.toEntity(novoUsuarioDTO);
        usuario = repository.save(usuario);
        return usuarioMapper.toDto(usuario);
    }

    @Transactional
    public UsuarioDTO atualizarUsuarioPorId(Long id, NovoUsuarioDTO novoUsuarioDTO) {

        authService.validarProprioUsuarioOuAdmin(id);

        try {
            Usuario usuario = repository.getReferenceById(id);
            novoUsuarioMapper.updateEntityFromDto(novoUsuarioDTO, usuario);
            usuario = repository.save(usuario);
            return usuarioMapper.toDto(usuario);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Usuário não encontrado com o ID: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deletarUsuarioPorId(Long id) {

        authService.validarProprioUsuarioOuAdmin(id);

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com o ID: " + id);
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Falha de integridade referencial");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);

        if (result.isEmpty()) {
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

        return usuario;
    }

    protected Usuario usuarioLogado() {

        try {
            String username = customUsuario.usernameDoUsuarioLogado();
            return repository.findByEmailIgnoreCase(username).get();
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("Email não localizado");
        }
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obterMeusDados() {
        Usuario usuario = usuarioLogado();
        return usuarioMapper.toDto(usuario);
    }
}
