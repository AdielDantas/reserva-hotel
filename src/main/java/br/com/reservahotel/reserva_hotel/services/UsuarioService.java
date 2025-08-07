package br.com.reservahotel.reserva_hotel.services;

import br.com.reservahotel.reserva_hotel.exceptions.ResourceNotFoundException;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioMinDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Usuario;
import br.com.reservahotel.reserva_hotel.model.mappers.UsuarioMapper;
import br.com.reservahotel.reserva_hotel.model.mappers.UsuarioMinMapper;
import br.com.reservahotel.reserva_hotel.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private UsuarioMinMapper usuarioMinMapper;

    @Transactional(readOnly = true)
    public UsuarioDTO buscarUsuarioPorIdComReservas(Long id) {
        Usuario usuario = repository.buscarUsuarioPorIdComReservas(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        return usuarioMapper.toDto(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarUsuarioPorEmailComReservas(String email) {
        Usuario usuario = repository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Usuário não encontrado com o Email: " + email));
        return usuarioMapper.toDto(usuario);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioMinDTO> buscarTodosUsuarosPaginados(Pageable pageable) {
        Page<Usuario> page = repository.findAll(pageable);
        return page.map(usuarioMinMapper::toUsuarioResumoDto);
    }
}
