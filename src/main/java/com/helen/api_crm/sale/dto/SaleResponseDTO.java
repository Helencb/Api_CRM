package com.helen.api_crm.sale.dto;


import com.helen.api_crm.sale.model.SaleStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponseDTO {
    private Long id;
    private String name;
    private BigDecimal totalValue;
    private Integer amount;
    private String description;
    private SaleStatus status;
    private String failureReason;
    private Long clientId;
    private Long sellerId;
    private LocalDateTime createdAt;
}
