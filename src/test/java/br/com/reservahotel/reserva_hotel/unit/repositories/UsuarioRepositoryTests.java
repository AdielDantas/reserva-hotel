package br.com.reservahotel.reserva_hotel.unit.repositories;

import br.com.reservahotel.reserva_hotel.factory.UsuarioFactory;
import br.com.reservahotel.reserva_hotel.model.entities.Usuario;
import br.com.reservahotel.reserva_hotel.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UsuarioRepositoryTests {

    @Autowired
    UsuarioRepository repository;

    private Long idExistente;
    private Long idInexistente;
    private String emailExistente;
    private String emailInexistente;
    private Usuario usuario;
    private PageImpl<Usuario> page;

    @BeforeEach
    void setUp() throws Exception {

        idExistente = 1L;
        idInexistente = 100L;
        emailExistente = "admin@gmail.com";
        emailInexistente = "naoexiste@gmail.com";
        usuario = UsuarioFactory.criarUsuarioAdmin();
        page = new PageImpl<>(List.of(usuario));
    }

    @Test
    void buscarUsuarioPorIdComReservasDeveRetornarUsuarioQuandoIdForExistente() {
        Optional<Usuario> resultado = repository.buscarUsuarioPorIdComReservas(idExistente);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(idExistente);
    }

    @Test
    void buscarUsuarioPorIdComReservasNaoDeveRetornarUsuarioQuandoIdNaoForExistente() {
        Optional<Usuario> resultado = repository.buscarUsuarioPorIdComReservas(idInexistente);

        assertThat(resultado).isNotPresent();
    }

    @Test
    void findByEmailIgnoreCaseDeveRetornarUsuarioQuandoEmailForExistente() {
        Optional<Usuario> resultado = repository.findByEmailIgnoreCase(emailExistente);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualToIgnoringCase(emailExistente);
    }

    @Test
    void findByEmailIgnoreCaseNaoDeveRetornarUsuarioQuandoEmailForExistente() {
        Optional<Usuario> resultado = repository.findByEmailIgnoreCase(emailInexistente);

        assertThat(resultado).isNotPresent();
    }

    @Test
    void findAllDeveRetornarUsuarioPaginados() {
        Pageable pageable = PageRequest.of(0, 3);
        Page<Usuario> resultado = repository.findAll(pageable);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).isNotEmpty();
        assertThat(resultado.getNumber()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(3);
    }
    @Test
    void saveDevePersistirNovoUsuarioEIncrementarIdQuandoIdForNull() {
        Usuario usuario = UsuarioFactory.criarUsuarioAdmin();
        usuario.setId(null);
        usuario.setEmail("usuario.teste.unique@email.com"); // Email único

        Usuario salvo = repository.save(usuario);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo(usuario.getNome());
        assertThat(salvo.getEmail()).isEqualTo("usuario.teste.unique@email.com");
        assertThat(salvo.getSenha()).isEqualTo(usuario.getSenha());

        assertTrue(repository.existsById(salvo.getId()));
    }

    @Test
    void deleteByIdDeveDeletarUsuarioQuandoIdForExistente() {
       repository.deleteById(idExistente);

       Optional<Usuario> resultado = repository.findById(idExistente);
       assertThat(resultado).isEmpty();
    }

    @Test
    void deleteByIdNaoDeveDeletarUsuarioQuandoIdForInexistente() {
        repository.deleteById(idExistente);

        Optional<Usuario> resultado = repository.findById(idInexistente);
        assertThat(resultado).isEmpty();
    }
}
