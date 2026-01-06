package com.helen.api_crm.dashboard.service;

import com.helen.api_crm.clients.repository.ClientRepository;
import com.helen.api_crm.dashboard.dto.DashboardSellerDTO;
import com.helen.api_crm.dashboard.dto.DashboardSummaryDTO;
import com.helen.api_crm.sale.dto.SaleResponseDTO;
import com.helen.api_crm.sale.mapper.SaleMapper;
import com.helen.api_crm.sale.model.SaleStatus;
import com.helen.api_crm.sale.repository.SaleRepository;
import com.helen.api_crm.seller.repository.SellerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DashboardService {
    private final ClientRepository clientRepository;
    private final SellerRepository sellerRepository;
    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;

    public DashboardSummaryDTO getSummary() {
        return new DashboardSummaryDTO(
                clientRepository.count(),
                sellerRepository.count(),
                saleRepository.totalSales(),
                saleRepository.sumTotalSalesAll()
        );
    }

    public DashboardSellerDTO getDashboardBySeller(Long sellerId) {
        Long totalSales = saleRepository.countBySellerId(sellerId);
        Long completedSales = saleRepository.countBySellerIdAndStatus(sellerId, SaleStatus.COMPLETED);
        Long canceledSales = saleRepository.countBySellerIdAndStatus(sellerId, SaleStatus.CANCELED);
        BigDecimal totalRevenue = saleRepository.sumTotalSales(sellerId);

        List<SaleResponseDTO> last5Sales = saleRepository
                .findTop5BySellerIdOrderByCreatedAtDesc(sellerId)
                .stream()
                .map(saleMapper::toDTO)
                .collect(Collectors.toList());

        return new DashboardSellerDTO(
                totalSales,
                completedSales,
                totalRevenue,
                canceledSales,
                last5Sales
        );
    }
}
