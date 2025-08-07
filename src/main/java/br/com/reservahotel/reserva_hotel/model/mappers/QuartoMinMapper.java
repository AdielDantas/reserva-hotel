package br.com.reservahotel.reserva_hotel.model.mappers;

import br.com.reservahotel.reserva_hotel.model.dto.QuartoMinDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Quarto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuartoMinMapper {

    QuartoMinDTO toDto(Quarto quarto);

    @Mapping(target = "reservas", ignore = true)
    Quarto toEntity(QuartoMinDTO quartoMinDTO);
}


