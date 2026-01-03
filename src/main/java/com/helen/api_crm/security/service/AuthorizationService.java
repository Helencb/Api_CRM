package com.helen.api_crm.security.service;

import com.helen.api_crm.sale.model.Sale;
import com.helen.api_crm.sale.repository.SaleRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("authorizationService")
public class AuthorizationService {

    private final SaleRepository saleRepository;

    public AuthorizationService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public boolean canAcessSale(Long saleId, Authentication authentication) {

        //Se for MANAGER, libera tudo
        if (authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MANAGER"))) {
            return true;
        }

        // Se for SELLER, valida se a venda pertence á ele
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        String emailLogado = authentication.getName();

        return sale.getSeller().getEmail().equals(emailLogado);
    }
}
