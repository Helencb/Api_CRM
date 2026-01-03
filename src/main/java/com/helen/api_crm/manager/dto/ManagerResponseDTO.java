package com.helen.api_crm.manager.dto;

public record ManagerResponseDTO(
    Long id,
    String nome,
    String email,
    Boolean active) {
}
