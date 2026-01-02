package com.helen.api_crm.sale.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SaleRequestDTO {
    @NotNull(message = "The name of the sale is required.")
    private String name;

    @NotNull(message = "Value is mandatory")
    @Positive(message = "The value must be greater than zero.")
    private BigDecimal value;

    @NotNull(message = "Quantity is mandatory")
    @Positive(message = "The quantity must be greater than zero.")
    private Double amount;

    private String description;

    @NotNull(message = "Customer ID is required.")
    private Long clientId;

    @NotNull(message = "Seller ID is required.")
    private Long sellerId;

}
