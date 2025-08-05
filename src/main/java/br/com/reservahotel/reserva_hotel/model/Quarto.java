package br.com.reservahotel.reserva_hotel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_quarto")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal valorDiária;
    private Boolean disponível;

    @OneToMany(mappedBy = "quartos")
    private List<Quarto> quartos = new ArrayList<>();
}
