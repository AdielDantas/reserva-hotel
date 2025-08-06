package br.com.reservahotel.reserva_hotel.model.entities;

import br.com.reservahotel.reserva_hotel.model.enums.StatusReserva;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "tb_reserva")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDate checkin;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDate checkout;
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    private StatusReserva status;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "quarto_id")
    private Quarto quarto;

    @PrePersist
    public void calcularValorTotal() {
        long dias = ChronoUnit.DAYS.between(checkin, checkout);
        this.valorTotal = quarto.getValorDiaria().multiply(BigDecimal.valueOf(dias));
    }
}
