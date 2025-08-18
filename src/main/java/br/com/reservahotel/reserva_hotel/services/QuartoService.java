package br.com.reservahotel.reserva_hotel.services;

import br.com.reservahotel.reserva_hotel.exceptions.DataBaseException;
import br.com.reservahotel.reserva_hotel.exceptions.ResourceNotFoundException;
import br.com.reservahotel.reserva_hotel.model.dto.QuartoDTO;
import br.com.reservahotel.reserva_hotel.model.dto.QuartoMinDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Quarto;
import br.com.reservahotel.reserva_hotel.model.enums.TipoQuarto;
import br.com.reservahotel.reserva_hotel.model.mappers.QuartoMapper;
import br.com.reservahotel.reserva_hotel.model.mappers.QuartoMinMapper;
import br.com.reservahotel.reserva_hotel.repositories.QuartoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QuartoService {

    @Autowired
    private QuartoRepository repository;

    @Autowired
    QuartoMapper quartoMapper;

    @Autowired
    QuartoMinMapper quartoMinMapper;

    @Transactional(readOnly = true)
    public QuartoDTO buscarQuartoPorId(Long id) {

        log.debug("Buscando quarto com ID: {}", id);

        Quarto quarto = repository.findById(id).orElseThrow(
                () -> {
                    log.error("Quarto não encontrado com ID: {}", id);
                    return new ResourceNotFoundException("Usuário não encontrado com o ID: " + id);
                });

        log.info("Quarto encontrado com ID: {}", quarto.getId());
        return quartoMapper.toDto(quarto);
    }

    @Transactional(readOnly = true)
    public List<QuartoDTO> listarTodosOsQuartos() {

        log.debug("Listando todos os quartos");

        List<QuartoDTO> quartos =  repository.findAll()
                .stream().map(quartoMapper::toDto).collect(Collectors.toList());

        log.info("Listagem de todos os quartos concluída - Total: {}", quartos.size());
        return quartos;
    }

    @Transactional(readOnly = true)
    public List<QuartoDTO> listarQuartosDisponiveis(LocalDate dataInicial, LocalDate dataFinal) {

        log.debug("Listando quartos disponíveis - Data inicial: {}, Data final: {}", dataInicial, dataFinal);

        if (dataInicial != null && dataFinal != null) {

            if (dataInicial.isAfter(dataFinal)) {
                log.error("Datas inválidas - Data inicial {} é depois da data final {}", dataInicial, dataFinal);
                throw new IllegalArgumentException("Data inicial não pode ser depois da data final");
            }

            List<Quarto> quartos = repository.findDisponiveisPorPeriodo(dataInicial, dataFinal);
            log.info("Busca de quartos disponíveis por período concluída - Total: {}", quartos.size());
            return quartos.stream().map(quartoMapper::toDto).toList();
        }

        List<Quarto> quartos = repository.findByDisponivelTrue();
        log.info("Busca de quartos disponíveis (sem período) concluída - Total: {}", quartos.size());
        return quartos.stream().map(quartoMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<QuartoDTO> listarQuartoPorTipo(String tipoStr) {

        log.debug("Listando quartos por tipo: {}", tipoStr);

        try {
            TipoQuarto tipo = TipoQuarto.valueOf(tipoStr.toUpperCase());
            List<Quarto> quartos = repository.findByTipo(tipo);

            log.info("Busca de quartos por tipo {} concluída - Total: {}", tipo, quartos.size());
            return quartos.stream().map(quartoMapper::toDto).collect(Collectors.toList());
        }
        catch (IllegalArgumentException e) {
            log.error("Tipo de quarto inválido: {}", tipoStr);
            throw new ResourceNotFoundException("Tipo não localizado");
        }
    }

    @Transactional
    public QuartoDTO criarQuarto(QuartoDTO quartoDTO) {

        log.info("Criando novo quarto do tipo: {}", quartoDTO.getTipo());

        Quarto quarto = quartoMapper.toEntity(quartoDTO);
        quarto = repository.save(quarto);

        log.info("Quarto criado com sucesso - ID: {}", quarto.getId());
        return quartoMapper.toDto(quarto);
    }

    @Transactional
    public QuartoDTO atualizarQuarto(Long id, QuartoDTO quartoDTO) {

        log.info("Atualizando quarto ID: {}", id);

        try {
            Quarto quarto = repository.getReferenceById(id);
            quartoMapper.updateEntityFromDto(quartoDTO, quarto);

            quarto = repository.save(quarto);
            log.info("Quarto ID: {} atualizado com sucesso", id);

            return quartoMapper.toDto(quarto);
        } catch (EntityNotFoundException e) {
            log.error("Erro ao atualizar quarto ID: {}. Motivo: não encontrado", id);
            throw new ResourceNotFoundException("Quarto não encontrado com o ID: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deletarQuarto(Long id) {

        log.info("Iniciando exclusão do quarto ID: {}", id);

        if (!repository.existsById(id)) {
            log.error("Falha ao excluir quarto ID: {}. Motivo: não encontrado", id);
            throw new ResourceNotFoundException("Quarto não encontrado com o ID: " + id);
        }
        try {
            repository.deleteById(id);
            log.info("Quarto ID: {} excluído com sucesso", id);
        }
        catch (DataIntegrityViolationException e) {
            log.error("Falha ao excluir quarto ID: {}. Motivo: violação de integridade referencial", id);
            throw new DataBaseException("Falha de integridade referencial");
        }
    }
}