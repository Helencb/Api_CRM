package com.helen.api_crm.manager.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ManagerResponseDTO {
    private Long id;
    private String nome;
    private String email;
}
