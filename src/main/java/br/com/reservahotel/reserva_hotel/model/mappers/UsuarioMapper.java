package br.com.reservahotel.reserva_hotel.model.mappers;

import br.com.reservahotel.reserva_hotel.model.dto.ReservaDTO;
import br.com.reservahotel.reserva_hotel.model.dto.UsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Reserva;
import br.com.reservahotel.reserva_hotel.model.entities.Usuario;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ReservaMapper.class, UsuarioMinMapper.class})
public interface UsuarioMapper {

    @Mapping(target = "reservas", source = "reservas")
    UsuarioDTO toDto(Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senha", ignore = true)
    Usuario toEntity(UsuarioDTO usuarioDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senha", ignore = true)
    void updateEntityFromDto(UsuarioDTO usuarioDTO, @MappingTarget Usuario usuario);
}

