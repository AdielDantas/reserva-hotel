package br.com.reservahotel.reserva_hotel.controllers;

import br.com.reservahotel.reserva_hotel.model.dto.NovoUsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioMinDTO;
import br.com.reservahotel.reserva_hotel.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping(value = "/id/{id}")
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorIdComReserva(@PathVariable Long id) {
        UsuarioDTO usuarioDTO = service.buscarUsuarioPorIdComReservas(id);
        return ResponseEntity.ok(usuarioDTO);
    }

    @GetMapping(value = "/email/{email}")
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorEmailComReserva(@PathVariable String email) {
        UsuarioDTO usuarioDTO = service.buscarUsuarioPorEmailComReservas(email);
        return ResponseEntity.ok(usuarioDTO);
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioMinDTO>> buscarTodosUsuarosPaginados(Pageable pageable) {
        Page<UsuarioMinDTO> page = service.buscarTodosUsuarosPaginados(pageable);
        return ResponseEntity.ok().body(page);
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> salvarNovoUsuario(@RequestBody NovoUsuarioDTO novoUsuarioDTO) {
        UsuarioDTO usuarioDTO = service.salvarNovoUsuario(novoUsuarioDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(usuarioDTO.getId())
                .toUri();
        return ResponseEntity.created(uri).body(usuarioDTO);
    }

    @PutMapping(value = "/id/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable Long id, @RequestBody NovoUsuarioDTO novoUsuarioDTO) {
        UsuarioDTO usuarioDTO = service.atualizarUsuarioPorId(id, novoUsuarioDTO);
        return ResponseEntity.ok(usuarioDTO);
    }
}
