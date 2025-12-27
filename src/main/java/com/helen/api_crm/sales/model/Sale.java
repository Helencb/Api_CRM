package com.helen.api_crm.sales.model;

import com.helen.api_crm.clients.model.Client;
import com.helen.api_crm.seller.model.Seller;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "sales")
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    private Seller seller;

    private Double amount;
    private String description;
    private LocalDateTime date;
}
