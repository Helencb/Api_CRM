package com.helen.api_crm.clients.repository;

import com.helen.api_crm.clients.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Page<Client> findAllByActiveTrue(Pageable pageable);

    Optional<Client> findByIdAndActiveTrue(Long id);
}