package com.helen.api_crm.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public record SellerDashboardResponseDTO(
        BigDecimal totalSalesAmount, //Soma de todas as vendas
        Long totalSalesCount, // quantidade total de vendas
        BigDecimal averageTicket, // ticket médio
        Long pendingSales, //status PENDING
        Long canceledSales, // status CANCELED

        Long sales, List<LastSaleDTO> lastSales //ultimas vendas
){}
