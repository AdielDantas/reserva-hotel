package br.com.reservahotel.reserva_hotel.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioMinDTO {

    private Long id;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail informado é inválido.")
    @Size(max = 100, message = "O e-mail não pode ter mais que 100 caracteres.")
    private String email;
}
