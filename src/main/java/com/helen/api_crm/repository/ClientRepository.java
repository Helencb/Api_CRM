package com.helen.api_crm.repository;

import com.helen.api_crm.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> id(Long id);
}