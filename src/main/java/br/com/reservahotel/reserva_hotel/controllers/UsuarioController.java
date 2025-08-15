package br.com.reservahotel.reserva_hotel.controllers;

import br.com.reservahotel.reserva_hotel.model.dto.NovoUsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioMinDTO;
import br.com.reservahotel.reserva_hotel.services.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENTE')")
    @GetMapping(value = "/me")
    public ResponseEntity<UsuarioDTO> obterMeusDados() {
        UsuarioDTO usuarioDTO = service.obterMeusDados();
        return ResponseEntity.ok(usuarioDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENTE')")
    @GetMapping(value = "/id/{id}")
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorIdComReserva(@PathVariable Long id) {
        UsuarioDTO usuarioDTO = service.buscarUsuarioPorIdComReservas(id);
        return ResponseEntity.ok(usuarioDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENTE')")
    @GetMapping(value = "/email/{email}")
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorEmailComReserva(@PathVariable String email) {
        UsuarioDTO usuarioDTO = service.buscarUsuarioPorEmailComReservas(email);
        return ResponseEntity.ok(usuarioDTO);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UsuarioMinDTO>> buscarTodosUsuarosPaginados(Pageable pageable) {
        Page<UsuarioMinDTO> page = service.buscarTodosUsuarosPaginados(pageable);
        return ResponseEntity.ok().body(page);
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> salvarNovoUsuario(@Valid @RequestBody NovoUsuarioDTO novoUsuarioDTO) {
        UsuarioDTO usuarioDTO = service.salvarNovoUsuario(novoUsuarioDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(usuarioDTO.getId())
                .toUri();
        return ResponseEntity.created(uri).body(usuarioDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENTE')")
    @PutMapping(value = "/id/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody NovoUsuarioDTO novoUsuarioDTO) {
        UsuarioDTO usuarioDTO = service.atualizarUsuarioPorId(id, novoUsuarioDTO);
        return ResponseEntity.ok(usuarioDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENTE')")
    @DeleteMapping(value = "/id/{id}")
    public ResponseEntity<Void> deletarUsuarioPorId(@PathVariable Long id) {
        service.deletarUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }
}
