package com.helen.api_crm.sale.dto;

import com.helen.api_crm.sale.model.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SaleRequestDTO {

    private String description;

    @NotNull(message = "Customer ID is required.")
    private Long clientId;

    @NotNull(message = "Seller ID is required.")
    private Long sellerId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal discount;

    @NotEmpty(message = "A sale must have at least one product.")
    @Valid
    private List<SaleItemRequestDTO> items;

}
