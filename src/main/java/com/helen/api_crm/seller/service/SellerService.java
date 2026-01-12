package com.helen.api_crm.seller.service;

import com.helen.api_crm.auth.repository.UserRepository;
import com.helen.api_crm.auth.model.User;
import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.manager.model.Manager;
import com.helen.api_crm.manager.repository.ManagerRepository;
import com.helen.api_crm.seller.dto.SellerRequestDTO;
import com.helen.api_crm.seller.dto.SellerResponseDTO;
import com.helen.api_crm.seller.mapper.SellerMapper;
import com.helen.api_crm.seller.model.Seller;
import com.helen.api_crm.seller.repository.SellerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final SellerMapper sellerMapper;
    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;

    //Criar vendedor
    @Transactional
    public SellerResponseDTO createSeller(SellerRequestDTO dto) {
        // Validar se email já existe
        if(userRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Email already in use");
        }

        // Obter o email do usuario logado (token)
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        //Buscar o usuario base para pegar o ID e validar a Role
        User userLogado = userRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Logged user not found"));

        if(userLogado.getRole() != Role.MANAGER) {
            throw new RuntimeException("Only managers can create sellers");
        }

        // Busca a entidade Manager correta usando o ID do usuário
        Manager manager = managerRepository.findById(userLogado.getId())
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        // Criar o User para autentificação
        Seller seller = new Seller();
        seller.setName(dto.name());
        seller.setEmail(dto.email());
        seller.setPassword(passwordEncoder.encode(dto.password()));
        seller.setRole(Role.SELLER);
        seller.setActive(true);
        seller.setManager(manager);

        sellerRepository.save(seller);

        return sellerMapper.toDTO(seller);
    }

    //Listar todos os vendedores
    public List<SellerResponseDTO> getAllSellers() {
        return sellerRepository.findAll()
                .stream()
                .map(sellerMapper::toDTO)
                .toList();
    }

    //Buscar vendedor por ID
    public SellerResponseDTO getSellerById(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        return sellerMapper.toDTO(seller);
    }
}

