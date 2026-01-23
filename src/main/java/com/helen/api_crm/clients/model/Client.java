package com.helen.api_crm.clients.model;

import com.helen.api_crm.sale.model.Sale;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    private String phone;

    @Column(nullable = false)
    private  boolean active = true;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Sale> sales;
}
