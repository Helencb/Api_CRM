package com.helen.api_crm.seller.dto;

import jakarta.validation.constraints.NotBlank;

public record SellerRequestDTO (
    @NotBlank(message = "Nome é obrigatório")
    String name,

    @NotBlank(message = "Email é obrigatório")
    String email,

    @NotBlank(message = "Senha é obrigatória")
    String password,

    String phone
)
{}
