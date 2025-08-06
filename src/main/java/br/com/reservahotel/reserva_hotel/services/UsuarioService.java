package br.com.reservahotel.reserva_hotel.services;

import br.com.reservahotel.reserva_hotel.exceptions.ResourceNotFoundException;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Usuario;
import br.com.reservahotel.reserva_hotel.model.mappers.UsuarioMapper;
import br.com.reservahotel.reserva_hotel.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioMapper mapper;

    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        return mapper.toDto(usuario);
    }
}
