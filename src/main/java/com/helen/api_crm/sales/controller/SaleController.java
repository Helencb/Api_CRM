package com.helen.api_crm.sales.controller;

import com.helen.api_crm.sales.service.SaleService;
import com.helen.api_crm.sales.dto.SaleRequestDTO;
import com.helen.api_crm.sales.dto.SaleResponseDTO;
import com.helen.api_crm.sales.model.Sale;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendas")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/client/{clientId}")
    public SaleResponseDTO crearSale(@RequestBody @Valid SaleRequestDTO request) {
        return saleService.createSale(request);
    }

    @GetMapping("/client/{clientId}")
    public List<Sale> listSales(@PathVariable Long clientId) {
        return saleService.listSalesByClient(clientId);
    }
}
