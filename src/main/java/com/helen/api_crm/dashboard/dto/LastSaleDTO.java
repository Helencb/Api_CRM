package com.helen.api_crm.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LastSaleDTO (
        Long saleId,
        String clientName,
        BigDecimal value,
        LocalDateTime date,
        String status
) {
}
