package br.com.reservahotel.reserva_hotel.services;

import br.com.reservahotel.reserva_hotel.exceptions.ResourceNotFoundException;
import br.com.reservahotel.reserva_hotel.model.dto.ReservaDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Reserva;
import br.com.reservahotel.reserva_hotel.model.mappers.ReservaMapper;
import br.com.reservahotel.reserva_hotel.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository repository;

    @Autowired
    private ReservaMapper reservaMapper;

    @Transactional(readOnly = true)
    public ReservaDTO buscarReservaPorId(Long id) {
        Reserva reserva = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Reserva não encontrada com o ID: " + id));
        return reservaMapper.toDto(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaDTO> buscarReservaPorIdDoUsuario(Long usuarioId) {
        List<Reserva> reservas = repository.findByUsuarioId(usuarioId);
        return reservas.stream().map(reservaMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public ReservaDTO criarReserva(ReservaDTO reservaDTO) {
        Reserva reserva = reservaMapper.toEntity(reservaDTO);
        reserva = repository.save(reserva);
        return reservaMapper.toDto(reserva);
    }
}
