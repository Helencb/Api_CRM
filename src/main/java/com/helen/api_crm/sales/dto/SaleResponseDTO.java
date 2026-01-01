package com.helen.api_crm.sales.dto;


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
    private BigDecimal value;
    private Integer amount;
    private boolean completed;
    private String failureReason;
    private String clientNome;
    private String sellerNome;
    private LocalDateTime date;
}
