package com.helen.api_crm.dashboard.service;

import com.helen.api_crm.dashboard.dto.LastSaleDTO;
import com.helen.api_crm.dashboard.dto.SellerDashboardResponseDTO;
import com.helen.api_crm.sale.model.SaleStatus;
import com.helen.api_crm.sale.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SellerDashboardService {
    private final SaleRepository saleRepository;

    public SellerDashboardResponseDTO getDashboard(Long sellerId) {
        BigDecimal totalSalesAmount = saleRepository.sumTotalSales(sellerId);
        Long totalSalesCount = saleRepository.countBySellerId(sellerId);
        BigDecimal averageTicket = totalSalesCount > 0
                ? totalSalesAmount.divide(
                        BigDecimal.valueOf(totalSalesCount),
                2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        Long completedSales = saleRepository.countBySellerIdAndStatus(sellerId, SaleStatus.COMPLETED);
        Long pendingSales = saleRepository.countBySellerIdAndStatus(sellerId, SaleStatus.PENDING);
        Long canceledSales = saleRepository.countBySellerIdAndStatus(sellerId, SaleStatus.CANCELED);

        List<LastSaleDTO> lastSales = saleRepository.findTop5BySellerIdOrderByCreatedAtDesc(sellerId)
                .stream()
                .map(sale -> new LastSaleDTO(
                        sale.getId()
                ,       sale.getClient().getName(),
                        sale.getTotalValue(),
                        sale.getCreatedAt(),
                        sale.getStatus().name()
                        )).toList();

        return new SellerDashboardResponseDTO(
                totalSalesAmount,
                totalSalesCount,
                averageTicket,
                completedSales,
                pendingSales,
                canceledSales,
                lastSales
        );
    }
}
