package com.helen.api_crm.dashboard.dto;

import java.math.BigDecimal;

public record DashboardSummaryDTO (
        Long totalClients,
        Long totalSellers,
        Long totalSales,
        BigDecimal totalRevenue
){ }
