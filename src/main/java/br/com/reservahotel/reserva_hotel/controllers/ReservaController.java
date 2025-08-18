package br.com.reservahotel.reserva_hotel.controllers;

import br.com.reservahotel.reserva_hotel.model.dto.ReservaDTO;
import br.com.reservahotel.reserva_hotel.services.ReservaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/reservas")
public class ReservaController {

    @Autowired
    private ReservaService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENTE')")
    @GetMapping(value = "/id/{id}")
    public ResponseEntity<ReservaDTO> buscarReservaPorId(@PathVariable Long id) {
        log.info("Iniciando busca da reserva com id {}", id);

        ReservaDTO reservaDTO = service.buscarReservaPorId(id);

        log.info("Reserva encontrada com sucesso: {}", reservaDTO);
        return ResponseEntity.ok(reservaDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENTE')")
    @GetMapping("/usuario")
    public ResponseEntity<List<ReservaDTO>> buscarReservaPorIdDoUsuario(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) String email) {

        log.info("Iniciando busca de reservas pelo usuário. usuarioId: {}, email: {}", usuarioId, email);

        List<ReservaDTO> reservas = service.buscarReservasPorUsuario(usuarioId, email);

        log.info("Busca concluída. Total de reservas encontradas: {}", reservas.size());
        return ResponseEntity.ok(reservas);
    }

    @PostMapping
    public ResponseEntity<ReservaDTO> criarReserva(@Valid @RequestBody ReservaDTO reservaDTO) {
        log.info("Iniciando criação de nova reserva: {}", reservaDTO);

        ReservaDTO reservaCriada = service.criarReserva(reservaDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(reservaCriada.getId())
                .toUri();

        log.info("Reserva criada com sucesso. ID: {}", reservaCriada.getId());
        return ResponseEntity.created(uri).body(reservaCriada);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENTE')")
    @PutMapping(value = "/id/{id}")
    public ResponseEntity<ReservaDTO> atualizarReserva(@PathVariable Long id, @Valid @RequestBody ReservaDTO reservaDTO) {
        log.info("Iniciando atualização da reserva de id {} com os novos dados {}", id, reservaDTO);

        ReservaDTO reservaAtualizada = service.atualizarReserva(id, reservaDTO);

        log.info("Reserva atualizada com sucesso: {}", reservaAtualizada);
        return ResponseEntity.ok(reservaAtualizada);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENTE')")
    @DeleteMapping(value = "/id/{id}")
    public ResponseEntity<Void> deletarReservaPorId(@PathVariable Long id) {
        log.info("Iniciando exclusão da reserva de id {}", id);

        service.deletarReservaPorId(id);

        log.info("Reserva com id {} excluída com sucesso", id);
        return ResponseEntity.noContent().build();
    }
}
