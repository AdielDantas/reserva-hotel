package br.com.reservahotel.reserva_hotel.services;

import br.com.reservahotel.reserva_hotel.exceptions.DataBaseException;
import br.com.reservahotel.reserva_hotel.exceptions.ResourceNotFoundException;
import br.com.reservahotel.reserva_hotel.model.dto.ReservaDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Quarto;
import br.com.reservahotel.reserva_hotel.model.entities.Reserva;
import br.com.reservahotel.reserva_hotel.model.mappers.ReservaMapper;
import br.com.reservahotel.reserva_hotel.repositories.QuartoRepository;
import br.com.reservahotel.reserva_hotel.repositories.ReservaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository repository;

    @Autowired
    private ReservaMapper reservaMapper;

    @Autowired
    private QuartoRepository quartoRepository;

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

        Quarto quarto = quartoRepository.findById(reservaDTO.getQuarto().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Quarto não encontrado"));

        if (!quarto.getDisponivel()) {
            throw new IllegalStateException("Este quarto já está reservado");
        }
            Reserva reserva = reservaMapper.toEntity(reservaDTO);
            reserva = repository.save(reserva);
            quarto.setDisponivel(false);
            return reservaMapper.toDto(reserva);
        }

    @Transactional
    public ReservaDTO atualizarReserva(Long id, ReservaDTO reservaDTO) {

        try {

            Reserva reservaExistente = repository.findById(reservaDTO.getQuarto().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Quarto não encontrado"));

            if (!reservaExistente.getQuarto().getId().equals(reservaDTO.getQuarto().getId())) {
                Quarto quartoAntigo = reservaExistente.getQuarto();
                quartoAntigo.setDisponivel(true);
                quartoRepository.save(quartoAntigo);
            }

            Quarto novoQuarto = quartoRepository.findById(reservaDTO.getQuarto().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Quarto não encontrado"));

            if (!novoQuarto.getDisponivel()) {
                throw new IllegalStateException("Este quarto já está reservado");
            }

            reservaMapper.updateEntityFromDto(reservaDTO, reservaExistente);
            Reserva reservaAtualizada = repository.save(reservaExistente);

            novoQuarto.setDisponivel(false);
            quartoRepository.save(novoQuarto);

            return reservaMapper.toDto(reservaAtualizada);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Reserva não encontrada com o ID: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deletarReservaPorId(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Reserva não encontrado com o ID: " + id);
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Falha de integridade referencial");
        }
    }
}
