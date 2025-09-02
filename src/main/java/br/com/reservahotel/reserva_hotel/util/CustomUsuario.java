package br.com.reservahotel.reserva_hotel.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CustomUsuario {

    public String usernameDoUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            return jwt.getClaim("username");
        }
        if (principal instanceof UserDetails user) {
            return user.getUsername();
        }
        if (principal instanceof String username) {
            return username;
        }

        throw new IllegalStateException("Tipo de principal inesperado: " + principal.getClass());
    }

}
