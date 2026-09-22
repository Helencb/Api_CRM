package com.helen.api_crm.manager.dto;

import jakarta.validation.constraints.Email;

public record ManagerRequestDTO(
    String name,

    @Email(message = "Email must be a valid address")
    String email,

    String password){}
