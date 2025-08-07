package br.com.reservahotel.reserva_hotel.model.mappers;

import br.com.reservahotel.reserva_hotel.model.dto.ReservaMinDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservaMinMapper {

    ReservaMinDTO toDto(Reserva reserva);

    @Mapping(target = "id", ignore = true)
    Reserva toEntity(ReservaMinDTO dto);
}


