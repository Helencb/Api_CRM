package com.helen.api_crm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Email(message = "Email é obrigatório")
            @NotBlank(message = "Email é obrigatório")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;
}
