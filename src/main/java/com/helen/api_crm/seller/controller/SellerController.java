package com.helen.api_crm.seller.controller;

import com.helen.api_crm.seller.dto.SellerRequestDTO;
import com.helen.api_crm.seller.dto.SellerResponseDTO;
import com.helen.api_crm.seller.model.Seller;
import com.helen.api_crm.seller.service.SellerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    // Criar Vemdedor
    @PostMapping
    public ResponseEntity<SellerResponseDTO> createSeller(@RequestBody SellerRequestDTO dto) {
        SellerResponseDTO response = sellerService.createSeller(dto);
        return ResponseEntity.ok(response);
    }

    // Listar todos os vendedores
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
}
