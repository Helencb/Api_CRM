package com.helen.api_crm.seller.controller;

import com.helen.api_crm.seller.dto.SellerRequestDTO;
import com.helen.api_crm.seller.dto.SellerResponseDTO;
import com.helen.api_crm.seller.dto.SellerUpdateDTO;
import com.helen.api_crm.seller.service.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sellers")
@Tag(name = "Vendedores", description = "Gestão de vendedores (Apenas anagers).")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Operation(summary = "Criar um novo vendedor", description = "Registra um novo vendedor.")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Vendedor criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
    })
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<SellerResponseDTO> createSeller(@RequestBody @Valid SellerRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sellerService.createSeller(dto));
    }

    @Operation(summary = "Listar vendedores", description = "Retorna uma lista de vendedores.")
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity<List<SellerResponseDTO>> getAllSellers() {
        List<SellerResponseDTO> sellers = sellerService.getAllSellers();
        return ResponseEntity.ok(sellers);
    }

    @Operation(summary = "Buscar vendedores por ID", description = "Retorna detalhes de um vendedores específico")
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<SellerResponseDTO> getSellerById(@PathVariable Long id) {
        SellerResponseDTO response = sellerService.getSellerById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualiza o Vendedores", description = "Retorna um vendedores específico para atualizar.")
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<SellerResponseDTO> updateSeller(@PathVariable Long id, @RequestBody @Valid SellerUpdateDTO dto) {
        return ResponseEntity.ok(sellerService.updateSeller(id, dto));
    }

    @Operation(summary = "Desativar o vendedor", description = "Desativa o vendedor.")
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }
}
