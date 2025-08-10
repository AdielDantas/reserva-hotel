package br.com.reservahotel.reserva_hotel.controllers;

import br.com.reservahotel.reserva_hotel.model.dto.ReservaDTO;
import br.com.reservahotel.reserva_hotel.services.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/reservas")
public class ReservaController {

    @Autowired
    private ReservaService service;

    @GetMapping(value = "/id/{id}")
    public ResponseEntity<ReservaDTO> buscarReservaPorId(@PathVariable Long id) {
        ReservaDTO reservaDTO = service.buscarReservaPorId(id);
        return ResponseEntity.ok(reservaDTO);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaDTO>> buscarReservaPorIdDoUsuario(@PathVariable Long usuarioId) {
        List<ReservaDTO> reservas = service.buscarReservaPorIdDoUsuario(usuarioId);
        return ResponseEntity.ok(reservas);
    }

    @PostMapping
    public ResponseEntity<ReservaDTO> criarReserva(@RequestBody ReservaDTO reservaDTO) {
        ReservaDTO reservaCriada = service.criarReserva(reservaDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(reservaCriada.getId())
                .toUri();
        return ResponseEntity.created(uri).body(reservaCriada);
    }

    @PutMapping(value = "/id/{id}")
    public ResponseEntity<ReservaDTO> atualizarReserva(@PathVariable Long id, @RequestBody ReservaDTO reservaDTO) {
        ReservaDTO reservaAtualizada = service.atualizarReserva(id, reservaDTO);
        return ResponseEntity.ok(reservaAtualizada);
    }

    @DeleteMapping(value = "/id/{id}")
    public ResponseEntity<Void> deletarReservaPorId(@PathVariable Long id) {
        service.deletarReservaPorId(id);
        return ResponseEntity.noContent().build();
    }
}
