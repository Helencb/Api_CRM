package com.helen.api_crm.manager.service;

import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.exception.BusinessException;
import com.helen.api_crm.manager.dto.ManagerRequestDTO;
import com.helen.api_crm.manager.dto.ManagerResponseDTO;
import com.helen.api_crm.manager.mapper.ManagerMapper;
import com.helen.api_crm.manager.model.Manager;
import com.helen.api_crm.manager.repository.ManagerRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final ManagerMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public ManagerService(ManagerRepository managerRepository,
                          ManagerMapper mapper,
                          PasswordEncoder passwordEncoder) {
        this.managerRepository = managerRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    public ManagerResponseDTO create(ManagerRequestDTO dto) {

        if(managerRepository.findByEmail(dto.email()).isPresent()) {
            throw new BusinessException("Email already registered");
        }

        //Cria o Manager
        Manager manager = mapper.toEntity(dto);
        manager.setPassword(passwordEncoder.encode(dto.password()));
        manager.setRole(Role.MANAGER);
        return mapper.toDTO(managerRepository.save(manager));
    }

    public List<ManagerResponseDTO> findAll() {
        return managerRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public ManagerResponseDTO findById(Long id) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Manager not found"));
        return mapper.toDTO(manager);
    }

    public void deactivate(Long id) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Manager not found"));

        manager.setActive(false);
        managerRepository.save(manager);
    }
}
