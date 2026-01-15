package com.helen.api_crm.seller.controller;

import com.helen.api_crm.seller.dto.SellerRequestDTO;
import com.helen.api_crm.seller.dto.SellerResponseDTO;
import com.helen.api_crm.seller.dto.SellerUpdateDTO;
import com.helen.api_crm.seller.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    // Criar Vendedor
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<SellerResponseDTO> createSeller(@RequestBody @Valid SellerRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sellerService.createSeller(dto));
    }

    // Listar todos os vendedores
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity<List<SellerResponseDTO>> getAllSellers() {
        List<SellerResponseDTO> sellers = sellerService.getAllSellers();
        return ResponseEntity.ok(sellers);
    }

    // Buscar vendedor por ID
    @GetMapping("/{id}")
    public ResponseEntity<SellerResponseDTO> getSellerById(@PathVariable Long id) {
        SellerResponseDTO response = sellerService.getSellerById(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<SellerResponseDTO> updateSeller(@PathVariable Long id, @RequestBody @Valid SellerUpdateDTO dto) {
        return ResponseEntity.ok(sellerService.updateSeller(id, dto));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }
}
