package br.com.reservahotel.reserva_hotel.model.mappers;

import br.com.reservahotel.reserva_hotel.model.dto.QuartoDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Quarto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ReservaMinMapper.class})
public interface QuartoMapper {

    QuartoDTO toDto(Quarto quarto);

    @Mapping(target = "id", ignore = true)
    Quarto toEntity(QuartoDTO quarto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(QuartoDTO quartoDTO, @MappingTarget Quarto quarto);
}

