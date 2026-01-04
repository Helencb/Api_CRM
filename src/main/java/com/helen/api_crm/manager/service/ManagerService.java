package com.helen.api_crm.manager.service;

import com.helen.api_crm.auth.Repository.UserRepository;
import com.helen.api_crm.auth.model.User;
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
    private final UserRepository userRepository;
    private final ManagerMapper managerMapper;

    public ManagerService(ManagerRepository managerRepository,
                          ManagerMapper mapper,
                          PasswordEncoder passwordEncoder, UserRepository userRepository, ManagerMapper managerMapper) {
        this.managerRepository = managerRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.managerMapper = managerMapper;
    }

    public ManagerResponseDTO create(ManagerRequestDTO dto) {
        User user = new User();
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(Role.MANAGER);
        user.setActive(true);

        userRepository.save(user);

        //Cria Manager e associar User
        Manager manager = mapper.toEntity(dto);
        manager.setUser(user);

        Manager savedManager = managerRepository.save(manager);

        return managerMapper.toDTO(savedManager);
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

        User user = manager.getUser();
        user.setActive(false);
        userRepository.save(user);
    }
}
