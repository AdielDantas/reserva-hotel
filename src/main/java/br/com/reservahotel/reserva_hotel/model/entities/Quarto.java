package br.com.reservahotel.reserva_hotel.model.entities;

import br.com.reservahotel.reserva_hotel.model.enums.TipoQuarto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_quarto")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal valorDiaria;
    private Boolean disponivel;

    @Enumerated(EnumType.STRING)
    private TipoQuarto tipo;

    @OneToMany(mappedBy = "quarto")
    private List<Reserva> reservas = new ArrayList<>();

}
