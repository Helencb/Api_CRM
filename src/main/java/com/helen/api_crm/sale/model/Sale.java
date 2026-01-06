package com.helen.api_crm.sale.model;

import com.helen.api_crm.clients.model.Client;
import com.helen.api_crm.seller.model.Seller;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    // Nome da venda ou do produto/serviço vendido
    @Column(nullable = false)
    private String name;

    // Valor monetário da venda
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalValue;

    // Status da venda (ex: PENDING, COMPLETED, CANCELED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus status;

    // Motivo da falha (caso a venda não tenha sido concluída)
    private String failureReason;

    // Muitas vendas podem pertencer a um cliente
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Muitas vendas podem pertencer a um vendedor
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    // Quantidade vendida (ex: número de itens)
    @Column(nullable = false)
    private Integer amount;

    // Descrição adicional da venda
    private String description;

    // Data e hora da venda
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
