package br.com.reservahotel.reserva_hotel.controllers;

import br.com.reservahotel.reserva_hotel.model.dto.QuartoDTO;
import br.com.reservahotel.reserva_hotel.model.dto.QuartoMinDTO;
import br.com.reservahotel.reserva_hotel.services.QuartoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/quartos")
public class QuartoController {

    @Autowired
    private QuartoService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<QuartoDTO> buscarQuartoPorId(@PathVariable Long id) {
        log.info("Requisição recebida para buscar quarto com ID: {}", id);

        QuartoDTO quartoDTO = service.buscarQuartoPorId(id);

        log.info("Quarto encontrado: {}", quartoDTO);
        return ResponseEntity.ok(quartoDTO);
    }

    @GetMapping
    public ResponseEntity<List<QuartoDTO>> listarTodosOsQuartos() {
        log.info("Requisição recebida para listar todos os quartos");

        List<QuartoDTO> quartos = service.listarTodosOsQuartos();

        log.info("Total de quartos encontrados: {}", quartos.size());
        return ResponseEntity.ok(quartos);
    }

    @GetMapping(value = "/disponiveis")
    public ResponseEntity<List<QuartoDTO>> listarQuartosDisponiveis(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {

        log.info("Requisição recebida para listar quartos disponíveis entre {} e {}", dataInicial, dataFinal);

        List<QuartoDTO> quartos = service.listarQuartosDisponiveis(dataInicial, dataFinal);

        log.info("Total de quartos disponíveis encontrados: {}", quartos.size());
        return ResponseEntity.ok(quartos);
    }

    @GetMapping(value = "/tipo")
    public ResponseEntity<List<QuartoDTO>> listarQuartoPorTipo(@RequestParam String tipo) {
        log.info("Requisição recebida para listar quartos do tipo: {}", tipo);

        List<QuartoDTO> quartos = service.listarQuartoPorTipo(tipo);

        log.info("Total de quartos encontrados para o tipo '{}': {}", tipo, quartos.size());
        return ResponseEntity.ok(quartos);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<QuartoDTO> criarQuarto(@Valid @RequestBody QuartoDTO novoQuartoDTO) {
        log.info("Requisição recebida para criar um novo quarto: {}", novoQuartoDTO);

        QuartoDTO quartoDTO = service.criarQuarto(novoQuartoDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(quartoDTO.getId())
                .toUri();

        log.info("Quarto criado com sucesso: {}", quartoDTO);
        return ResponseEntity.created(uri).body(quartoDTO);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<QuartoDTO> atualizarQuarto(@PathVariable Long id, @Valid @RequestBody QuartoDTO quartoAtualizado) {
        log.info("Requisição recebida para atualizar quarto com ID: {} com novos dados: {}", id, quartoAtualizado);

        QuartoDTO quartoDTO = service.atualizarQuarto(id, quartoAtualizado);

        log.info("Quarto atualizado com sucesso: {}", quartoDTO);
        return ResponseEntity.ok(quartoDTO);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletarQuarto(@PathVariable Long id) {
        log.info("Requisição recebida para deletar quarto com ID: {}", id);

        service.deletarQuarto(id);

        log.info("Quarto com ID {} deletado com sucesso", id);
        return ResponseEntity.noContent().build();
    }
}
