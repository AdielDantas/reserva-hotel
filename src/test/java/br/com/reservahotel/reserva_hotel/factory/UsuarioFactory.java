package br.com.reservahotel.reserva_hotel.factory;

import br.com.reservahotel.reserva_hotel.model.entities.Role;
import br.com.reservahotel.reserva_hotel.model.entities.Usuario;

public class UsuarioFactory {

    public static Usuario criarUsuarioAdmin() {
        Usuario usuario = new Usuario(1L, "Usuario Admin Teste", "usuarioadmin@gmail.com", "$2a$10$N7SkKCa3r17ga.i.dF9iy.BFUBL2n3b6Z1CWSZWi/qy7ABq/E6VpO");
        usuario.addRole(new Role(1L, "ROLE_CLIENTE"));
        return usuario;
    }

    public static Usuario criarUsuarioCliente() {
        Usuario usuario = new Usuario(2L, "Usuario Cliente Teste", "usuariocliente@gmail.com", "$2a$10$N7SkKCa3r17ga.i.dF9iy.BFUBL2n3b6Z1CWSZWi/qy7ABq/E6VpO");
        usuario.addRole(new Role(1L, "ROLE_CLIENTE"));
        return usuario;
    }
}
