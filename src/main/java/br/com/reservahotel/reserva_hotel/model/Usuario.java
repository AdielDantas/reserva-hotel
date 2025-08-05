package br.com.reservahotel.reserva_hotel.model;

import br.com.reservahotel.reserva_hotel.model.enums.Perfil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_usuario")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long nome;
    private String email;
    private String senha;
    private Perfil perfil;

    @OneToMany(mappedBy = "usuario")
    private List<Reserva> reservas = new ArrayList<>();
}
