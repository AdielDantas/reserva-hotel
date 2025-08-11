package br.com.reservahotel.reserva_hotel.services;

import br.com.reservahotel.reserva_hotel.exceptions.DataBaseException;
import br.com.reservahotel.reserva_hotel.exceptions.ResourceNotFoundException;
import br.com.reservahotel.reserva_hotel.model.dto.QuartoDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Quarto;
import br.com.reservahotel.reserva_hotel.model.mappers.QuartoMapper;
import br.com.reservahotel.reserva_hotel.repositories.QuartoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuartoService {

    @Autowired
    private QuartoRepository repository;

    @Autowired
    QuartoMapper quartoMapper;

    @Transactional(readOnly = true)
    public QuartoDTO buscarQuartoPorId(Long id) {
        Quarto quarto = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
                return quartoMapper.toDto(quarto);
    }

    @Transactional(readOnly = true)
    public List<QuartoDTO> listarTodosOsQuartos() {
        return repository.findAll()
                .stream().map(quartoMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public QuartoDTO criarQuarto(QuartoDTO quartoDTO) {
        Quarto quarto = quartoMapper.toEntity(quartoDTO);
        quarto = repository.save(quarto);
        return quartoMapper.toDto(quarto);
    }

    @Transactional
    public QuartoDTO atualizarQuarto(Long id, QuartoDTO quartoDTO) {
        try {
            Quarto quarto = repository.getReferenceById(id);
            quartoMapper.updateEntityFromDto(quartoDTO, quarto);
            quarto = repository.save(quarto);
            return quartoMapper.toDto(quarto);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Quarto não encontrado com o ID: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deletarQuarto(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com o ID: " + id);
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Falha de integridade referencial");
        }
    }
}
