package com.helen.api_crm.seller.dto;

import jakarta.validation.constraints.Email;

public record SellerUpdateDTO(
        String name,

        @Email(message = "Email inválido")
        String email,

        String phone,

        String password
) {}
