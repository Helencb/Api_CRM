package com.helen.api_crm.manager.repository;

import com.helen.api_crm.clients.model.Client;
import com.helen.api_crm.manager.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    List<Client> id(Long id);
}
