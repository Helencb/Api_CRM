package com.helen.api_crm.sale.repository;

import com.helen.api_crm.sale.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByClientId(Long clientId);
    List<Sale> findBySellerId(Long sellerId);
    List<Sale> findByCompleted(boolean completed);
}
