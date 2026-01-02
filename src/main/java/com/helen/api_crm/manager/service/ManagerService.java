package com.helen.api_crm.manager.service;

import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.manager.dto.ManagerRequestDTO;
import com.helen.api_crm.manager.dto.ManagerResponseDTO;
import com.helen.api_crm.manager.model.Manager;
import com.helen.api_crm.manager.repository.ManagerRepository;
import com.helen.api_crm.auth.Repository.UserRepository;
import com.helen.api_crm.auth.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ManagerService(ManagerRepository managerRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.managerRepository = managerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ManagerResponseDTO create(ManagerRequestDTO dto) {
        //Validações básicas
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }

        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long");
        }

        //Verifica se o email já está em uso
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        //Cria o User (login)
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.ROLE_MANAGER);

        userRepository.save(user);

        //Cria o Manager
        Manager manager = new Manager();
        manager.setNome(dto.getNome());
        manager.setUser(user);

        Manager savedManager = managerRepository.save(manager);

        //Retorna o DTO de resposta
        return new ManagerResponseDTO(
            savedManager.getId(),
            savedManager.getNome(),
            savedManager.getUser().getEmail()
        );
    }
}
