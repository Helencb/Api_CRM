package com.helen.api_crm.sales.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SaleRequestDTO {

    @NotNull(message = "ID do cliente é obrigatório")
    private Long clientId;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser maior que zero")
    private Double amount;

    private String description;

    @NotNull(message = "Data da venda é obrigatória")
    private LocalDateTime date;

}
