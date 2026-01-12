package com.helen.api_crm.manager.service;

import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.exception.BusinessException;
import com.helen.api_crm.manager.dto.ManagerRequestDTO;
import com.helen.api_crm.manager.dto.ManagerResponseDTO;
import com.helen.api_crm.manager.mapper.ManagerMapper;
import com.helen.api_crm.manager.model.Manager;
import com.helen.api_crm.manager.repository.ManagerRepository;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ManagerMapper managerMapper;

    public ManagerResponseDTO createManager(ManagerRequestDTO dto) {
        Manager manager = new Manager();
        manager.setEmail(dto.email());
        manager.setPassword(passwordEncoder.encode(dto.password()));
        manager.setRole(Role.MANAGER);
        manager.setActive(true);
        manager.setName(dto.name());

        managerRepository.save(manager);

        return managerMapper.toDTO(manager);
    }

    public List<ManagerResponseDTO> findAll() {
        return managerRepository.findAll()
                .stream()
                .map(managerMapper::toDTO)
                .toList();
    }

    public ManagerResponseDTO findById(Long id) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Manager not found"));
        return managerMapper.toDTO(manager);
    }

    public void deactivate(Long id) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Manager not found"));

        manager.setActive(false);
        managerRepository.save(manager);
    }
}
