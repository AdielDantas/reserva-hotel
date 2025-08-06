package br.com.reservahotel.reserva_hotel.repositories;

import br.com.reservahotel.reserva_hotel.model.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
