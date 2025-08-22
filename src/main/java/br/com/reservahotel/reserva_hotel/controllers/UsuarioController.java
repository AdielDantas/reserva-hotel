package br.com.reservahotel.reserva_hotel.controllers;

import br.com.reservahotel.reserva_hotel.model.dto.NovoUsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioMinDTO;
import br.com.reservahotel.reserva_hotel.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping(value = "/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENTE')")
    @GetMapping(value = "/me")
    public ResponseEntity<UsuarioDTO> obterMeusDados() {
        log.info("Recebida requisição GET /usuarios/me");

        UsuarioDTO usuarioDTO = service.obterMeusDados();

        log.info("Requisição GET /usuarios/me concluída com sucesso - Usuário ID={}", usuarioDTO.getId());
        return ResponseEntity.ok(usuarioDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENTE')")
    @GetMapping(value = "/id/{id}")
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorIdComReserva(@PathVariable Long id) {
        log.info("Recebida requisição GET /usuarios/id/{}", id);

        UsuarioDTO usuarioDTO = service.buscarUsuarioPorIdComReservas(id);

        log.info("Requisição GET /usuarios/id/{} concluída com sucesso", id);
        return ResponseEntity.ok(usuarioDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENTE')")
    @GetMapping(value = "/email/{email}")
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorEmailComReserva(@PathVariable String email) {
        log.info("Recebida requisição GET /usuarios/email/{}", email);

        UsuarioDTO usuarioDTO = service.buscarUsuarioPorEmailComReservas(email);

        log.info("Requisição GET /usuarios/email/{} concluída com sucesso", email);
        return ResponseEntity.ok(usuarioDTO);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UsuarioMinDTO>> buscarTodosUsuarosPaginados(Pageable pageable) {
        log.info("Recebida requisição GET /usuarios - Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<UsuarioMinDTO> page = service.buscarTodosUsuariosPaginados(pageable);

        log.info("Requisição GET /usuarios concluída com sucesso - Total elementos: {}", page.getTotalElements());
        return ResponseEntity.ok().body(page);
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> salvarNovoUsuario(@Valid @RequestBody NovoUsuarioDTO novoUsuarioDTO) {
        log.info("Recebida requisição POST /usuarios - Criando usuário com email: {}", novoUsuarioDTO.getEmail());

        UsuarioDTO usuarioDTO = service.salvarNovoUsuario(novoUsuarioDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(usuarioDTO.getId())
                .toUri();

        log.info("Usuário criado com sucesso - ID: {}", usuarioDTO.getId());
        return ResponseEntity.created(uri).body(usuarioDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENTE')")
    @PutMapping(value = "/id/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody NovoUsuarioDTO novoUsuarioDTO) {
        log.info("Recebida requisição PUT /usuarios/id/{} - Atualizando dados", id);

        UsuarioDTO usuarioDTO = service.atualizarUsuarioPorId(id, novoUsuarioDTO);

        log.info("Usuário ID: {} atualizado com sucesso", id);
        return ResponseEntity.ok(usuarioDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENTE')")
    @DeleteMapping(value = "/id/{id}")
    public ResponseEntity<Void> deletarUsuarioPorId(@PathVariable Long id) {
        log.info("Recebida requisição DELETE /usuarios/id/{}", id);

        service.deletarUsuarioPorId(id);

        log.info("Usuário ID: {} excluído com sucesso", id);
        return ResponseEntity.noContent().build();
    }
}
