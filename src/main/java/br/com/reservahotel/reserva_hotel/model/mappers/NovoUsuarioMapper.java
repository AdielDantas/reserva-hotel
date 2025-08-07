package br.com.reservahotel.reserva_hotel.model.mappers;

import br.com.reservahotel.reserva_hotel.model.dto.NovoUsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ReservaMapper.class})
public interface NovoUsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reservas", ignore = true) // Ignora reservas na criação
    Usuario toEntity(NovoUsuarioDTO novoUsuarioDTO);

    @Mapping(target = "senha", ignore = true) // Não retorna senha no DTO
    NovoUsuarioDTO toDto(Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    void updateEntityFromDto(NovoUsuarioDTO novoUsuarioDTO, @MappingTarget Usuario usuario);
}

