package com.helen.api_crm.seller.repository;

import com.helen.api_crm.seller.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository  extends JpaRepository<Seller, Long> {
}
