package com.helen.api_crm.manager.repository;

import com.helen.api_crm.manager.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findByEmail(String email);
}
