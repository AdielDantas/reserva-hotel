package br.com.reservahotel.reserva_hotel.model.mappers;

import br.com.reservahotel.reserva_hotel.model.dto.RoleDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDTO toDto(Role role);

    Role toEntity(RoleDTO roleDTO);


}
