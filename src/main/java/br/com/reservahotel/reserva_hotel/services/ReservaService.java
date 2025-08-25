package br.com.reservahotel.reserva_hotel.services;

import br.com.reservahotel.reserva_hotel.exceptions.DataBaseException;
import br.com.reservahotel.reserva_hotel.exceptions.ForbiddenException;
import br.com.reservahotel.reserva_hotel.exceptions.ResourceNotFoundException;
import br.com.reservahotel.reserva_hotel.model.dto.ReservaDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Quarto;
import br.com.reservahotel.reserva_hotel.model.entities.Reserva;
import br.com.reservahotel.reserva_hotel.model.entities.Usuario;
import br.com.reservahotel.reserva_hotel.model.mappers.ReservaMapper;
import br.com.reservahotel.reserva_hotel.repositories.QuartoRepository;
import br.com.reservahotel.reserva_hotel.repositories.ReservaRepository;
import br.com.reservahotel.reserva_hotel.repositories.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReservaService {

    @Autowired
    private ReservaRepository repository;

    @Autowired
    private ReservaMapper reservaMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private QuartoRepository quartoRepository;

    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public ReservaDTO buscarReservaPorId(Long id) {

        log.debug("Buscando reserva de ID: {}", id);

        Reserva reserva = repository.findById(id).orElseThrow(
                () -> {
                    log.error("Reserva não encontrada com ID: {}", id);
                    return new ResourceNotFoundException("Reserva não encontrada com o ID: " + id);
                });

        authService.validarProprioUsuarioOuAdmin(reserva.getUsuario().getId());
        log.info("Usuário validado: {}", reserva.getUsuario().getEmail());

        return reservaMapper.toDto(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaDTO> buscarReservasPorUsuario(@Nullable Long usuarioId, @Nullable String email) {

        log.info("Iniciando busca de reserva do usuário id: {}, Email: {}",usuarioId, email);

        log.debug("Resolvendo ID do usuário: ID {}, Email {}", usuarioId, email);
        Long alvoId = authService.resolveUsuarioId(usuarioId, email);
        log.debug("Id do usuário resolvido: {}", alvoId);

        log.debug("Validando permissões do usuário ID: {}", alvoId);
        authService.validarProprioUsuarioOuAdmin(alvoId);

        List<Reserva> reservas = repository.findByUsuarioId(alvoId);
        if (reservas.isEmpty()) {

            log.warn("Nenhuma reserva encontrada para o usuário ID: {}", alvoId);
            throw new ResourceNotFoundException("Este usuário não tem reservas");
        }

        return reservas.stream().map(reservaMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public ReservaDTO criarReserva(ReservaDTO reservaDTO) {

        log.info("Iniciando criação de reserva no quart9o ID: {}, pelo usuário ID: {}",
                reservaDTO.getQuarto().getId(), reservaDTO.getUsuario().getId());

        Quarto quarto = quartoRepository.findById(reservaDTO.getQuarto().getId())
                .orElseThrow(() -> {
                    log.error("Quarto não encontrado com o ID: {}", reservaDTO.getQuarto().getId());
                    return new ResourceNotFoundException("Quarto não encontrado");
                });

        if (!quarto.getDisponivel()) {
            log.warn("O quarto ID: {} não está disponível pois já possui uma reserva", reservaDTO.getQuarto().getId());
            throw new IllegalStateException("Este quarto já está reservado");
        }

        log.debug("Quarto ID: {} disponível. Prosseguindo com a criação da reserva", reservaDTO.getQuarto().getId());

            Reserva reserva = reservaMapper.toEntity(reservaDTO);
            reserva = repository.save(reserva);
            log.info("Reserva ID: {} criada com sucesso para o usuário {}",reserva.getId(), reserva.getUsuario());

            quarto.setDisponivel(false);
            return reservaMapper.toDto(reserva);
    }

    @Transactional
    public ReservaDTO atualizarReserva(Long id, ReservaDTO reservaDTO) {
        log.info("Iniciando atualização da reserva com ID: {}", id);

        try {
            // Busca a reserva existente
            Reserva reservaExistente = repository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Reserva não encontrada com ID: {}", id);
                        return new ResourceNotFoundException("Reserva não encontrada com o ID: " + id);
                    });

            // Validação de permissão do usuário
            authService.validarProprioUsuarioOuAdmin(reservaExistente.getUsuario().getId());
            log.debug("Validação de permissão concluída para o usuário ID: {}", reservaExistente.getUsuario().getId());

            // Se o quarto da reserva mudou, verifica disponibilidade e atualiza
            if (!reservaExistente.getQuarto().getId().equals(reservaDTO.getQuarto().getId())) {
                Quarto novoQuarto = quartoRepository.findById(reservaDTO.getQuarto().getId())
                        .orElseThrow(() -> {
                            log.error("Novo quarto não encontrado com ID: {}", reservaDTO.getQuarto().getId());
                            return new ResourceNotFoundException("Quarto não encontrado com o ID: " + reservaDTO.getQuarto().getId());
                        });

                if (!novoQuarto.getDisponivel()) {
                    log.warn("Tentativa de atualizar reserva para quarto indisponível - ID: {}", novoQuarto.getId());
                    throw new IllegalStateException("O quarto selecionado não está disponível para reserva");
                }

                // Libera o quarto antigo
                Quarto quartoAntigo = reservaExistente.getQuarto();
                quartoAntigo.setDisponivel(true);
                quartoRepository.save(quartoAntigo);

                // Reserva o novo quarto
                novoQuarto.setDisponivel(false);
                quartoRepository.save(novoQuarto);

                log.info("Alterando reserva do quarto {} para o quarto {}", quartoAntigo.getId(), novoQuarto.getId());
                reservaExistente.setQuarto(novoQuarto);
            }

            // Atualiza os dados da reserva
            reservaMapper.updateEntityFromDto(reservaDTO, reservaExistente);

            // Salva e retorna a reserva atualizada
            Reserva reservaAtualizada = repository.save(reservaExistente);
            log.info("Reserva atualizada com sucesso - ID: {}", reservaAtualizada.getId());

            return reservaMapper.toDto(reservaAtualizada);

        } catch (ResourceNotFoundException | IllegalStateException | ForbiddenException e) {
            log.warn("Erro de negócio ao atualizar reserva: {}", e.getMessage());

            throw e;
        } catch (Exception e) {

            log.error("Erro inesperado ao atualizar reserva com ID: {}", id, e);
            throw new RuntimeException("Erro inesperado ao atualizar reserva", e);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deletarReservaPorId(Long id) {

        authService.validarProprioUsuarioOuAdmin(id);

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
