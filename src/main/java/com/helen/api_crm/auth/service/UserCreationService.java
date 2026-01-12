package com.helen.api_crm.auth.service;

import com.helen.api_crm.auth.repository.UserRepository;
import com.helen.api_crm.auth.model.User;
import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.manager.model.Manager;
import com.helen.api_crm.manager.repository.ManagerRepository;
import com.helen.api_crm.seller.model.Seller;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreationService {

    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;

    // Cria um novo usuário
    @Transactional
    public User createUser(String email, String password, Role role) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedUserEmail = authentication.getName();

        User loggedUser = userRepository.findByEmail(loggedUserEmail)
                .orElseThrow(() -> new RuntimeException(("Usuário autenticado não encontrado")));

        if(loggedUser.getRole() != Role.MANAGER) {
            throw new RuntimeException("Apenas gerentes podem criar novos usuários");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email já está em uso");
        }

        User newUser;

        if (role == Role.MANAGER) {
            Manager manager = new Manager();
            manager.setName("Novo Gerente");
            newUser = manager;
        } else if (role == Role.SELLER) {
            Seller seller = new Seller();
            seller.setName("Novo Vendedor");
            // Associa o vendedor ao gerente que está criando ele
            if(loggedUser instanceof Manager) {
                seller.setManager((Manager)loggedUser);
            } else {
                Manager mgr = managerRepository.findById(loggedUser.getId())
                        .orElseThrow(() -> new RuntimeException("Gerente não encontrado para associar ao vendedor"));
                seller.setManager(mgr);
            }
            newUser = seller;
        } else {
            newUser = new User();
        }

        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(role);
        newUser.setActive(true);

        return userRepository.save(newUser);
    }

}
