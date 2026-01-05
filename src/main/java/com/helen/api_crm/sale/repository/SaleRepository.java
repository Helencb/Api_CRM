package com.helen.api_crm.sale.repository;

import com.helen.api_crm.sale.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByClientId(Long clientId);
    List<Sale> findBySellerId(Long sellerId);
    List<Sale> findByCompleted(boolean completed);

    @Query("SELECT COALESCE(SUM(s.totalValue), 0) FROM Sale s WHERE s.status = 'COMPLETED'")
    BigDecimal totalRevenue();

    @Query("SELECT COUNT(s) FROM Sale s")
    Long totalSales();
}
