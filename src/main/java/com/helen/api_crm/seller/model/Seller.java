package com.helen.api_crm.seller.model;

import com.helen.api_crm.manager.model.Manager;
import com.helen.api_crm.sale.model.Sale;
import com.helen.api_crm.auth.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "sellers")
@PrimaryKeyJoinColumn(name = "id")
public class Seller extends User{
    @Column(nullable = false)
    private String name;

    private String phone;

    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private Manager manager;

    @OneToMany(mappedBy = "seller")
    private List<Sale> sales;
}
