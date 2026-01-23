package com.helen.api_crm.seller.service;

import com.helen.api_crm.auth.repository.UserRepository;
import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.exception.BusinessException;
import com.helen.api_crm.exception.ResourceNotFoundException;
import com.helen.api_crm.manager.model.Manager;
import com.helen.api_crm.manager.repository.ManagerRepository;
import com.helen.api_crm.seller.dto.SellerRequestDTO;
import com.helen.api_crm.seller.dto.SellerResponseDTO;
import com.helen.api_crm.seller.dto.SellerUpdateDTO;
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

    @Transactional
    public SellerResponseDTO createSeller(SellerRequestDTO dto) {
        if(userRepository.existsByEmail(dto.email())) {
            throw new BusinessException("Email already in use");
        }
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailLogado = authentication.getName();

        boolean isManager = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER"));

        if (!isManager) {
            throw new BusinessException("Only managers can create sellers");
        }

        Manager manager = managerRepository.findByEmail(emailLogado)
                    .orElseThrow(() -> new ResourceNotFoundException("Logged Manager not found in database"));

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

    public List<SellerResponseDTO> getAllSellers() {
        return sellerRepository.findAllByActiveTrue()
                .stream()
                .map(sellerMapper::toDTO)
                .toList();
    }

    public SellerResponseDTO getSellerById(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        return sellerMapper.toDTO(seller);
    }

    @Transactional
    public SellerResponseDTO updateSeller(Long id, SellerUpdateDTO dto) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        if (dto.name() != null && !dto.name().isBlank()) {
            seller.setName(dto.name());
        }

        if (dto.phone() != null){
            seller.setPhone(dto.phone());
        }

        if (dto.email() != null && !dto.email().isBlank() && !dto.email().equals(seller.getEmail())) {
            if (userRepository.existsByEmail(dto.email())) {
                throw new BusinessException("Email already in use");
            }
            seller.setEmail(dto.email());
        }

        if (dto.password() != null && !dto.password().isBlank()) {
            seller.setPassword(passwordEncoder.encode(dto.password()));
        }
        return sellerMapper.toDTO(sellerRepository.save(seller));
    }

    @Transactional
    public void deleteSeller(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        seller.setActive(false);
        sellerRepository.save(seller);
    }
}

