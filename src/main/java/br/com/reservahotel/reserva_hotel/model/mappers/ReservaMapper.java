package br.com.reservahotel.reserva_hotel.model.mappers;

import br.com.reservahotel.reserva_hotel.model.dto.ReservaDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {QuartoMinMapper.class, UsuarioMinMapper.class})
public interface ReservaMapper {

    ReservaDTO toDto(Reserva reserva);

    @Mapping(target = "id", ignore = true)
    Reserva toEntity(ReservaDTO reservaDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(ReservaDTO reservaDTO, @MappingTarget Reserva reserva);
}

