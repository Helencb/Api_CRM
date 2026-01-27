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

    Page<Client> findAllBySellerIdAndActiveTrue(Long sellerId, Pageable pageable);

    Optional<Client> findByIdAndSellerIdAndActiveTrue(Long id, Long sellerId);

    Optional<Client> findByIdAndActiveTrue(Long id);

    Optional<Client> findByEmail(String email);

}