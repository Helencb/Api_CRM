package com.helen.api_crm.sales.repository;

import com.helen.api_crm.sales.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByClient(Long client);
}
