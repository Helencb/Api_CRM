package com.helen.api_crm.dashboard.service;

import com.helen.api_crm.clients.repository.ClientRepository;
import com.helen.api_crm.dashboard.dto.DashboardSummaryDTO;
import com.helen.api_crm.sale.repository.SaleRepository;
import com.helen.api_crm.seller.repository.SellerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class DashboardService {
    private final ClientRepository clientRepository;
    private final SellerRepository sellerRepository;
    private final SaleRepository saleRepository;

    public DashboardSummaryDTO getSummary() {
        return new DashboardSummaryDTO(
                clientRepository.count(),
                sellerRepository.count(),
                saleRepository.totalSales(),
                saleRepository.sumTotalSalesAll()
        );
    }
}
