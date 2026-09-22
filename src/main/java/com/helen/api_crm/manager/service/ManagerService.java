package com.helen.api_crm.manager.service;

import com.helen.api_crm.auth.repository.UserRepository;
import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.exception.BusinessException;
import com.helen.api_crm.exception.ResourceNotFoundException;
import com.helen.api_crm.manager.dto.ManagerRequestDTO;
import com.helen.api_crm.manager.dto.ManagerResponseDTO;
import com.helen.api_crm.manager.mapper.ManagerMapper;
import com.helen.api_crm.manager.model.Manager;
import com.helen.api_crm.manager.repository.ManagerRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ManagerMapper managerMapper;
    private final UserRepository userRepository;

    public ManagerResponseDTO createManager(ManagerRequestDTO dto) {
        if (dto.name() == null || dto.name().isBlank()) {
            throw new BusinessException("Name is required.");
        }
        if (dto.email() == null || dto.email().isBlank()) {
            throw new BusinessException("Email is required.");
        }
        if (dto.password() == null || dto.password().isBlank()) {
            throw new BusinessException("Password is required.");
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new BusinessException("E-mail já cadastrado no sistema.");
        }

        Manager manager = new Manager();
        manager.setEmail(dto.email());
        manager.setPassword(passwordEncoder.encode(dto.password()));
        manager.setRole(Role.MANAGER);
        manager.setActive(true);
        manager.setName(dto.name());

        managerRepository.save(manager);

        return managerMapper.toDTO(manager);
    }

    public Page<ManagerResponseDTO> findAll(Pageable pageable) {
        return managerRepository.findAll(pageable)
                .map(managerMapper::toDTO);
    }

    public ManagerResponseDTO findById(Long id) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        return managerMapper.toDTO(manager);
    }

    @Transactional
    public ManagerResponseDTO updateManager(Long id, ManagerRequestDTO dto) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        if (dto.name() != null && !dto.name().isBlank()) {
            manager.setName(dto.name());
        }
        if(dto.email() != null && !dto.email().isBlank() && !dto.email().equals(manager.getEmail())) {
            if (userRepository.existsByEmail(dto.email())) {
                throw new BusinessException("E-mail já está em uso por outro usuário.");
            }
            manager.setEmail(dto.email());
        }

        if (dto.password() != null && !dto.password().isBlank()) {
            manager.setPassword(passwordEncoder.encode(dto.password()));
        }
        return managerMapper.toDTO(managerRepository.save(manager));
    }

    @Transactional
    public void deactivate(Long id) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        manager.setActive(false);
        managerRepository.save(manager);
    }
}
