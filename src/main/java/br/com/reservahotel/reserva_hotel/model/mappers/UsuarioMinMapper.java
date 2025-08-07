package br.com.reservahotel.reserva_hotel.model.mappers;

import br.com.reservahotel.reserva_hotel.model.dto.UsuarioMinDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMinMapper {

    UsuarioMinDTO toUsuarioResumoDto(Usuario usuario);

    @Mapping(target = "reservas", ignore = true)
    @Mapping(target = "senha", ignore = true)
    Usuario toUsuarioEntity(UsuarioMinDTO dto);
}

