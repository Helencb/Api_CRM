package com.helen.api_crm.seller.service;

import com.helen.api_crm.auth.Repository.UserRepository;
import com.helen.api_crm.auth.model.User;
import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.seller.dto.SellerRequestDTO;
import com.helen.api_crm.seller.dto.SellerResponseDTO;
import com.helen.api_crm.seller.mapper.SellerMapper;
import com.helen.api_crm.seller.model.Seller;
import com.helen.api_crm.seller.repository.SellerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final SellerMapper sellerMapper;

    //Criar vendedor
    public SellerResponseDTO createSeller(SellerRequestDTO dto) {
        // Criar o User para autentificação
        Seller seller = new Seller();
        seller.setPassword(passwordEncoder.encode(dto.password()));
        seller.setRole(Role.SELLER);
        seller.setActive(true);

        sellerRepository.save(seller);

        return sellerMapper.toDTO(seller);
    }

    //Listar todos os vendedores
    public List<SellerResponseDTO> getAllSellers() {
        return sellerRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private SellerResponseDTO toResponseDTO(Seller seller) {
        return sellerMapper.toDTO(seller);
    }

    //Buscar vendedor por ID
    public SellerResponseDTO getSellerById(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        return sellerMapper.toDTO(seller);
    }


}
