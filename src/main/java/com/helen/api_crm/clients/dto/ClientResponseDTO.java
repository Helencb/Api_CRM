package com.helen.api_crm.clients.dto;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Transactional
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String telefone;

}
