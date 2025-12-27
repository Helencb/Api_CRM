package com.helen.api_crm.security.config;

import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.security.Repository.UserRepository;
import com.helen.api_crm.security.model.User;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class loadData {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public void LoadData(UserRepository userRepository,
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        if (userRepository.count() == 0) {

            User manager = new User();
            manager.setEmail("admin@crm.com");
            manager.setPassword(passwordEncoder.encode("admin123"));
            manager.setRole(Role.ROLE_MANAGER);

            userRepository.save(manager);
        }
    }
}
