package com.helen.api_crm.seller.service;

import com.helen.api_crm.auth.Repository.UserRepository;
import com.helen.api_crm.auth.model.User;
import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.seller.dto.SellerRequestDTO;
import com.helen.api_crm.seller.dto.SellerResponseDTO;
import com.helen.api_crm.seller.mapper.SellerMapper;
import com.helen.api_crm.seller.model.Seller;
import com.helen.api_crm.seller.repository.SellerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final SellerMapper sellerMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public SellerService(SellerRepository sellerRepository, SellerMapper sellerMapper, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.sellerRepository = sellerRepository;
        this.sellerMapper = sellerMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    //Criar vendedor
    public SellerResponseDTO createSeller(SellerRequestDTO dto) {
        // Criar o User para autentificação
        User user = new User();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.SELLER);
        user.setActive(true);

        userRepository.save(user);

        //Criar o Seller e associar o User
        Seller seller = sellerMapper.toEntity(dto);
        seller.setUser(user);

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
