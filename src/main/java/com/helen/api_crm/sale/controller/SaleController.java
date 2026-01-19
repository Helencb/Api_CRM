package com.helen.api_crm.sale.controller;

import com.helen.api_crm.sale.dto.SaleCancelRequestDTO;
import com.helen.api_crm.sale.service.SaleService;
import com.helen.api_crm.sale.dto.SaleRequestDTO;
import com.helen.api_crm.sale.dto.SaleResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    // Criar uma nova venda
    @PreAuthorize("hasAnyRole('MANAGER','SELLER')")
    @PostMapping
    public ResponseEntity<SaleResponseDTO> createSale(@RequestBody @Valid SaleRequestDTO saleRequestDTO) {
        SaleResponseDTO response = saleService.createSale(saleRequestDTO);
        return ResponseEntity.ok(response);
    }

    //Concluir venda
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/complete")
    public ResponseEntity<SaleResponseDTO> completeSale(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.completeSale(id));
    }

    // Cancelar Venda
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<SaleResponseDTO> cancelSale(@PathVariable Long id, @RequestBody @Valid SaleCancelRequestDTO dto) {
        return ResponseEntity.ok(saleService.cancelSale(id, dto.getFailureReason()));
    }

    //Listar todas as vendas
    @PreAuthorize("hasAnyRole('MANAGER','SELLER')")
    @GetMapping
    public ResponseEntity<List<SaleResponseDTO>> getAllSales() {
        List<SaleResponseDTO> sales = saleService.getAllSales();
        return ResponseEntity.ok(sales);
    }

    //Buscar venda por ID
    @PreAuthorize("@authorizationService.canAcessSale(#id, authentication)")
    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> getSaleById(@PathVariable Long id) {
        SaleResponseDTO response = saleService.getSaleById(id);
        return ResponseEntity.ok(response);
    }


}
