package com.helen.api_crm.dashboard.service;

import com.helen.api_crm.clients.repository.ClientRepository;
import com.helen.api_crm.dashboard.dto.DashboardSummaryDTO;
import com.helen.api_crm.sale.repository.SaleRepository;
import com.helen.api_crm.seller.repository.SellerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

        @InjectMocks
        private DashboardService dashboardService;

        @Mock
        private ClientRepository clientRepository;

        @Mock
        private SellerRepository sellerRepository;

        @Mock
        private SaleRepository saleRepository;

        @Test
        void shouldReturnDashboardSummary() {

            when(saleRepository.sumTotalSalesAll())
                    .thenReturn(BigDecimal.valueOf(5000.0));

            when(saleRepository.totalSales())
                    .thenReturn(20L);

            when(clientRepository.count())
                    .thenReturn(10L);

            when(sellerRepository.count())
                    .thenReturn(5L);

            DashboardSummaryDTO result =
                    dashboardService.getSummary();

            assertEquals(10L, result.totalClients());
            assertEquals(5L, result.totalSellers());
            assertEquals(20L, result.totalSales());
            assertEquals(BigDecimal.valueOf(5000.0), result.totalRevenue());
        }
    }

