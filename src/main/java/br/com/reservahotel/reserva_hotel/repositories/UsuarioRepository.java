package br.com.reservahotel.reserva_hotel.repositories;

import br.com.reservahotel.reserva_hotel.model.entities.Reserva;
import br.com.reservahotel.reserva_hotel.model.entities.Usuario;
import br.com.reservahotel.reserva_hotel.projections.UserDetailsProjection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.reservas WHERE u.id = :id")
    Optional<Usuario> buscarUsuarioPorIdComReservas(@Param("id") Long id);

    @Query(nativeQuery = true, value = """
        SELECT u.email AS username, 
               u.senha AS password, 
               r.id AS roleId, 
               r.authority
        FROM tb_usuario u
        INNER JOIN tb_usuario_role ur ON u.id = ur.usuario_id
        INNER JOIN tb_role r ON r.id = ur.role_id
        WHERE u.email = :email
    """)
    List<UserDetailsProjection> searchUserAndRolesByEmail(@Param("email") String email);
}
