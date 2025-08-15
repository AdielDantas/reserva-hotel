package br.com.reservahotel.reserva_hotel.model.mappers;

import br.com.reservahotel.reserva_hotel.model.dto.RoleDTO;
import br.com.reservahotel.reserva_hotel.model.entities.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    // Mapeamento básico Role <-> RoleDTO
    RoleDTO toDto(Role role);
    Role toEntity(RoleDTO roleDTO);

    // Conversão especial para UsuarioMapper
    @Named("rolesToStringList")
    default List<String> mapRoles(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(Role::getAuthority)
                .collect(Collectors.toList());
    }

    @Named("stringListToRoles")
    default Set<Role> mapStrings(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return Collections.emptySet();
        }
        return roleNames.stream()
                .map(roleName -> {
                    Role role = new Role();
                    role.setAuthority(roleName);
                    return role;
                })
                .collect(Collectors.toSet());
    }
}