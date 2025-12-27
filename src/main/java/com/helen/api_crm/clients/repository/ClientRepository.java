package com.helen.api_crm.clients.repository;

import com.helen.api_crm.clients.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> id(Long id);
}