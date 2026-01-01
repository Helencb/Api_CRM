package com.helen.api_crm.sales.controller;

import com.helen.api_crm.sales.dto.SaleCancelRequestDTO;
import com.helen.api_crm.sales.service.SaleService;
import com.helen.api_crm.sales.dto.SaleRequestDTO;
import com.helen.api_crm.sales.dto.SaleResponseDTO;
import org.springframework.http.ResponseEntity;
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
    @PostMapping
    public ResponseEntity<SaleResponseDTO> createSale(@RequestBody SaleRequestDTO saleRequestDTO) {
        SaleResponseDTO response = saleService.createSale(saleRequestDTO);
        return ResponseEntity.ok(response);
    }

    //Concluir venda
    @PutMapping("/{id}/complete")
    public ResponseEntity<SaleResponseDTO> completeSale(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.completeSale(id));
    }

    // Cancelar Venda
    @PutMapping("/{id}/cancel")
    public ResponseEntity<SaleResponseDTO> cancelSale(@PathVariable Long id, @RequestBody SaleCancelRequestDTO dto) {
        return ResponseEntity.ok(saleService.cancelSale(id, dto.getFailureReason()));
    }

    //Listar todas as vendas
    @GetMapping
    public ResponseEntity<List<SaleResponseDTO>> getAllSales() {
        List<SaleResponseDTO> sales = saleService.getAllSales();
        return ResponseEntity.ok(sales);
    }

    //Buscar venda por ID
    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> getSaleById(@PathVariable Long id) {
        SaleResponseDTO response = saleService.getSaleById(id);
        return ResponseEntity.ok(response);
    }


}
