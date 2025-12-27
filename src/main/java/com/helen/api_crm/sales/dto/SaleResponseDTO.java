package com.helen.api_crm.sales.dto;

import jakarta.transaction.Transactional;
import lombok.*;

@Transactional
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponseDTO {
    private Long id;
    private String nome;
    private Long clientId;
    private Double amount;
    private String description;
    private String date;
}
