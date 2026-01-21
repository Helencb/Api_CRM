package com.helen.api_crm.sale.controller;

import com.helen.api_crm.sale.dto.SaleCancelRequestDTO;
import com.helen.api_crm.sale.service.SaleService;
import com.helen.api_crm.sale.dto.SaleRequestDTO;
import com.helen.api_crm.sale.dto.SaleResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
@Tag(name = "vendas", description = "Gerenciamento de operações de vendas")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @Operation(summary = "Criar nova venda", description = "Registra uma nova venda no sistema com status PENDING")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Venda criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente ou Vendedor não encontrado"),
    })

    @PreAuthorize("hasAnyRole('MANAGER','SELLER')")
    @PostMapping
    public ResponseEntity<SaleResponseDTO> createSale(@RequestBody @Valid SaleRequestDTO saleRequestDTO) {
        SaleResponseDTO response = saleService.createSale(saleRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Finalizar venda", description = "Altera o status da venda para COMPLETED")
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/complete")
    public ResponseEntity<SaleResponseDTO> completeSale(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.completeSale(id));
    }

    @Operation(summary = "Cancelar venda", description = "Cancela uma venda existente, exigindo motivo")
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<SaleResponseDTO> cancelSale(@PathVariable Long id, @RequestBody @Valid SaleCancelRequestDTO dto) {
        return ResponseEntity.ok(saleService.cancelSale(id, dto.getFailureReason()));
    }

    @Operation(summary = "Listar vendas", description = "Retorna uma lista paginada de vendas")
    @PreAuthorize("hasAnyRole('MANAGER','SELLER')")
    @GetMapping
    public ResponseEntity<Page<SaleResponseDTO>> getAllSales(
            @Parameter(hidden = true)
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SaleResponseDTO> sales = saleService.getAllSales(pageable);
        return ResponseEntity.ok(sales);
    }


    @Operation(summary = "Buscar venda por ID", description = "Retorna detalhes de uma venda específica")
    @PreAuthorize("@authorizationService.canAcessSale(#id, authentication)")
    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> getSaleById(@PathVariable Long id) {
        SaleResponseDTO response = saleService.getSaleById(id);
        return ResponseEntity.ok(response);
    }


}
