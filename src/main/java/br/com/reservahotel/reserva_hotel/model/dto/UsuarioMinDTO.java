package br.com.reservahotel.reserva_hotel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioMinDTO {

    private Long id;
    private String nome;
    private String email;
}
