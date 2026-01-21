package com.helen.api_crm.sale.repository;

import com.helen.api_crm.sale.model.Sale;
import com.helen.api_crm.sale.model.SaleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    Long countBySellerId(Long sellerId);

    Long countBySellerIdAndStatus(Long sellerId, SaleStatus status);

    List<Sale> findTop5BySellerIdOrderByCreatedAtDesc(Long sellerId);

    @Query("SELECT COUNT(s) FROM Sale s")
    Long totalSales();

    @Query("SELECT COALESCE(SUM(s.totalValue),0) FROM Sale s WHERE s.status = 'COMPLETED'")
    BigDecimal sumTotalSalesAll();

    @Query("""
            SELECT  COALESCE(SUM(s.totalValue), 0)
            FROM Sale s
            Where s.seller.id = :sellerId
            AND s.status = 'COMPLETED'
            """)

    BigDecimal sumTotalSales(@Param("sellerId") Long sellerId);


    @EntityGraph(attributePaths =  {"client", "seller"})
    Page<Sale> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"client", "seller"})
    Page<Sale> findBySellerId(Long sellerId, Pageable pageable);

}
