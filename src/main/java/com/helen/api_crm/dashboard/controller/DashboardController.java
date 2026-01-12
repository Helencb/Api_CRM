package com.helen.api_crm.dashboard.controller;

import com.helen.api_crm.dashboard.dto.DashboardSellerDTO;
import com.helen.api_crm.dashboard.dto.DashboardSummaryDTO;
import com.helen.api_crm.dashboard.dto.SellerDashboardResponseDTO;
import com.helen.api_crm.dashboard.service.DashboardService;
import com.helen.api_crm.dashboard.service.SellerDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    private final SellerDashboardService sellerDashboardService;

    public DashboardController(DashboardService dashboardService, SellerDashboardService sellerDashboardService) {
        this.dashboardService = dashboardService;
        this.sellerDashboardService = sellerDashboardService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<DashboardSummaryDTO> getSummary() {
        DashboardSummaryDTO summary = dashboardService.getSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/seller/summary/{selleId}")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<DashboardSellerDTO> getDashboardBySeller(@PathVariable Long selleId) {
        DashboardSellerDTO dashboard = dashboardService.getDashboardBySeller(selleId);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/seller/dashboard/{sellerId}")
    @PreAuthorize("hasAnyRole('MANAGER','SELLER')")
    public ResponseEntity<SellerDashboardResponseDTO> getSellerDashboard(@PathVariable Long sellerId) {
        return ResponseEntity.ok(sellerDashboardService.getDashboard(sellerId));
    }
}
