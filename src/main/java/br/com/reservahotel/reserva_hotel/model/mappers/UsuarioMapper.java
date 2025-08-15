package br.com.reservahotel.reserva_hotel.model.mappers;

import br.com.reservahotel.reserva_hotel.model.dto.UsuarioDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Usuario;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {ReservaMapper.class, UsuarioMinMapper.class, RoleMapper.class}
)
public interface UsuarioMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStringList")
    @Mapping(target = "reservas", source = "reservas")
    UsuarioDTO toDto(Usuario usuario);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "stringListToRoles")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senha", ignore = true)
    Usuario toEntity(UsuarioDTO usuarioDTO);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "stringListToRoles")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senha", ignore = true)
    void updateEntityFromDto(UsuarioDTO usuarioDTO, @MappingTarget Usuario usuario);
}