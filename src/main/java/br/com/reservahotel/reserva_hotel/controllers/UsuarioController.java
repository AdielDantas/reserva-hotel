package br.com.reservahotel.reserva_hotel.controllers;

import br.com.reservahotel.reserva_hotel.model.dto.UsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioMinDTO;
import br.com.reservahotel.reserva_hotel.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
