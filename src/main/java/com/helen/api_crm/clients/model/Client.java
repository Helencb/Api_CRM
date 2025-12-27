package com.helen.api_crm.clients.model;

import com.helen.api_crm.sales.model.Sale;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String telefone;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Sale> sales = new ArrayList<Sale>();
}
