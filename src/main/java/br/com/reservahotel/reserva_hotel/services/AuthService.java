package br.com.reservahotel.reserva_hotel.services;

import br.com.reservahotel.reserva_hotel.exceptions.ForbiddenException;
import br.com.reservahotel.reserva_hotel.exceptions.ResourceNotFoundException;
import br.com.reservahotel.reserva_hotel.model.entities.Usuario;
import br.com.reservahotel.reserva_hotel.repositories.UsuarioRepository;
import br.com.reservahotel.reserva_hotel.util.CustomUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CustomUsuario customUsuario;

    public void validarProprioUsuarioOuAdmin(Long usuarioId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("Usuário não autenticado");
        }

        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("Usuário não autenticado");
        }

        Usuario usuario = usuarioLogado();

        if (!usuario.hasRole("ROLE_ADMIN") && !usuario.getId().equals(usuarioId)) {
            throw new ForbiddenException("Acesso negado. Você só pode acessar seus próprios dados");
        }
    }

    public void validarSomenteAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("Usuário não autenticado");
        }

        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("Usuário não autenticado");
        }

        Usuario usuario = usuarioLogado();

        if (!usuario.hasRole("ROLE_ADMIN")) {
            throw new ForbiddenException("Acesso negado. Apenas administradores podem executar essa ação.");
        }
    }


    public Long resolveUsuarioId(@Nullable Long usuarioId, @Nullable String email) {
        if (usuarioId != null) return usuarioId;

        if (email != null && !email.isBlank()) {
            return usuarioRepository.findByEmailIgnoreCase(email.trim())
                    .map(Usuario::getId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        }

        throw new IllegalArgumentException("Informe o ID ou email do usuário.");
    }

    public Usuario usuarioLogado() {
        String username = customUsuario.usernameDoUsuarioLogado();
        return usuarioRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Email não localizado"));
    }
}


