package com.helen.api_crm.dashboard.controller;

import com.helen.api_crm.dashboard.dto.DashboardSummaryDTO;
import com.helen.api_crm.dashboard.dto.SellerDashboardResponseDTO;
import com.helen.api_crm.dashboard.service.DashboardService;
import com.helen.api_crm.dashboard.service.SellerDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "Métricas e indicadores de desempenho.")
public class DashboardController {

    private final DashboardService dashboardService;

    private final SellerDashboardService sellerDashboardService;

    public DashboardController(DashboardService dashboardService, SellerDashboardService sellerDashboardService) {
        this.dashboardService = dashboardService;
        this.sellerDashboardService = sellerDashboardService;
    }

    @Operation(summary = "Resumo Global da Empresa", description = "Exibe totais de vendas e receita de toda a empresa. Apenas para Gerentes.")
    @GetMapping("/company-summary")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<DashboardSummaryDTO> getCompanySummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    @Operation(summary = "Dashboard do vendedor", description = "Métricas detalhadas de um vendedor. Gerentes podem ver qualquer um. Vendedores veem apenas o seu.")
    @GetMapping("/seller/{sellerId}")
    @PreAuthorize("@authorizationService.isSellerOwner(#sellerId, authentication)")
    public ResponseEntity<SellerDashboardResponseDTO> getSellerDashboard(@PathVariable Long sellerId) {
        SellerDashboardResponseDTO dashboard = sellerDashboardService.getDashboard(sellerId);
        return ResponseEntity.ok(dashboard);
    }
}
