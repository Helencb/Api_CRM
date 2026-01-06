package com.helen.api_crm.dashboard.dto;

import com.helen.api_crm.sale.dto.SaleResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSellerDTO {
    private Long TotalSales;
    private Long canceledSales;
    private BigDecimal totalRevenue;
    private Long completedSales;
    private List<SaleResponseDTO> last5Sales;
}
