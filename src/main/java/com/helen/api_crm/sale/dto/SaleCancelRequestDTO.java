package com.helen.api_crm.sale.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class SaleCancelRequestDTO {
    @NotBlank(message = "Cancellation reason is required")
    private String failureReason;
}
