package com.helen.api_crm.sale.repository;

import com.helen.api_crm.sale.model.Sale;
import com.helen.api_crm.sale.model.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    // Vendas por cliente
    List<Sale> findByClientId(Long clientId);

    // Vendas por vendedor
    List<Sale> findBySellerId(Long sellerId);

    //Conta todas as vendas de um seller
    Long countBySellerId(Long sellerId);

    // Conta vendas de um seller por status
    Long countBySellerIdAndStatus(Long sellerId, SaleStatus status);

    // Busca as ultimas 5 vendas do seller
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


}
