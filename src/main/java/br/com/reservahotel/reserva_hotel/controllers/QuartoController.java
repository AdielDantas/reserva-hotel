package br.com.reservahotel.reserva_hotel.controllers;

import br.com.reservahotel.reserva_hotel.model.dto.QuartoDTO;
import br.com.reservahotel.reserva_hotel.services.QuartoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
