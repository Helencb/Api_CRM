package com.helen.api_crm.security.config;

import com.helen.api_crm.auth.model.User;
import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.auth.repository.UserRepository;
import com.helen.api_crm.manager.model.Manager;
import com.helen.api_crm.manager.repository.ManagerRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class LoadDataClass {

    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;

    public LoadDataClass(UserRepository userRepository,
                         ManagerRepository managerRepository,
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.managerRepository = managerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void LoadData() {

        String email = "admin@crm.com";

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return; // manager já existe
        }

        try {
            Manager admin = new Manager();
            admin.setName("Admin Principal");

            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.MANAGER);
            admin.setActive(true);

            managerRepository.save(admin);

            log.info("Usuário Admin criado com sucesso: {}", admin.getEmail());
    } catch (Exception e) {
        log.error("Erro ao criar usuário Admin Inicial", e);
        }
    }
}
