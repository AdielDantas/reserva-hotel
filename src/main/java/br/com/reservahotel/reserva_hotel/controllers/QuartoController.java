package br.com.reservahotel.reserva_hotel.controllers;

import br.com.reservahotel.reserva_hotel.model.dto.QuartoDTO;
import br.com.reservahotel.reserva_hotel.model.dto.QuartoMinDTO;
import br.com.reservahotel.reserva_hotel.services.QuartoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/quartos")
public class QuartoController {

    @Autowired
    private QuartoService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<QuartoDTO> buscarQuartoPorId(@PathVariable Long id) {
        QuartoDTO quartoDTO = service.buscarQuartoPorId(id);
        return ResponseEntity.ok(quartoDTO);
    }

    @GetMapping
    public ResponseEntity<List<QuartoDTO>> listarTodosOsQuartos() {
        List<QuartoDTO> quartos = service.listarTodosOsQuartos();
        return ResponseEntity.ok(quartos);
    }

    @GetMapping(value = "/disponiveis")
    public ResponseEntity<List<QuartoMinDTO>> listarQuartosDisponiveis() {
        List<QuartoMinDTO> quartos = service.listarQuartosDisponiveis();
        return ResponseEntity.ok(quartos);
    }

    @PostMapping
    public ResponseEntity<QuartoDTO> criarQuarto(@RequestBody QuartoDTO novoQuartoDTO) {
        QuartoDTO quartoDTO = service.criarQuarto(novoQuartoDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(quartoDTO.getId())
                .toUri();
        return ResponseEntity.created(uri).body(quartoDTO);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<QuartoDTO> atualizarQuarto(@PathVariable Long id, @RequestBody QuartoDTO quartoAtualizado) {
        QuartoDTO quartoDTO = service.atualizarQuarto(id, quartoAtualizado);
        return ResponseEntity.ok(quartoDTO);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletarQuarto(@PathVariable Long id) {
        service.deletarQuarto(id);
        return ResponseEntity.noContent().build();
    }
}
