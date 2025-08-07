package br.com.reservahotel.reserva_hotel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NovoUsuarioDTO extends UsuarioDTO {

    private String senha;
}
