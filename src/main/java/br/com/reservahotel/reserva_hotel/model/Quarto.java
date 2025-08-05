package br.com.reservahotel.reserva_hotel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_quarto")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Quarto {

    private Long id;
    private BigDecimal valorDiária;
    private Boolean disponível;
}
