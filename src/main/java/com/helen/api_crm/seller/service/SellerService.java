package com.helen.api_crm.seller.service;

import com.helen.api_crm.seller.dto.SellerRequestDTO;
import com.helen.api_crm.seller.dto.SellerResponseDTO;
import com.helen.api_crm.seller.mapper.SellerMapper;
import com.helen.api_crm.seller.model.Seller;
import com.helen.api_crm.seller.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;

    private final SellerMapper sellerMapper;

    public SellerService(SellerRepository sellerRepository, SellerMapper sellerMapper) {
        this.sellerRepository = sellerRepository;
        this.sellerMapper = sellerMapper;
    }

    //Criar vendedor
    public SellerResponseDTO createSeller(SellerRequestDTO dto) {
        Seller seller = sellerMapper.toEntity(dto);
        Seller savedSeller = sellerRepository.save(seller);
        return sellerMapper.toDTO(savedSeller);
    }

    //Listar todos os vendedores
    public List<SellerResponseDTO> getAllSellers() {
        return sellerRepository.findAll()
                .stream()
                .map(sellerMapper::toDTO)
                .collect(Collectors.toList());
    }

    //Buscar vendedor por ID
    public SellerResponseDTO getSellerById(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        return sellerMapper.toDTO(seller);
    }


}
