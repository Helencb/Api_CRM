package com.helen.api_crm.security.service;

import com.helen.api_crm.sale.model.Sale;
import com.helen.api_crm.sale.repository.SaleRepository;
import com.helen.api_crm.seller.model.Seller;
import com.helen.api_crm.seller.repository.SellerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("authorizationService")
public class AuthorizationService {

    private final SaleRepository saleRepository;
    private final SellerRepository sellerRepository;

    public AuthorizationService(SaleRepository saleRepository, SellerRepository sellerRepository) {
        this.saleRepository = saleRepository;
        this.sellerRepository = sellerRepository;
    }

    public boolean canAcessSale(Long saleId, Authentication authentication) {
        if (authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("MANAGER"))) {
            return true;
        }

        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
        return sale.getSeller().getEmail().equals(authentication.getName());
    }

    public boolean isSellerOwner(Long sellerId, Authentication authentication) {
        if(authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("MANAGER"))) {
            return true;
        }
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        return seller.getEmail().equals(authentication.getName());
    }
}
