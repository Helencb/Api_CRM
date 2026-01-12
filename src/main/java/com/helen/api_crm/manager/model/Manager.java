package com.helen.api_crm.manager.model;

import com.helen.api_crm.auth.model.User;
import com.helen.api_crm.seller.model.Seller;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "managers")
@PrimaryKeyJoinColumn(name = "id")
public class Manager extends User{

    @Column(nullable = false)
    private String name;

    // Um gerente pode ter vários vendedores
    @OneToMany(mappedBy = "manager")
    private List<Seller> sellers;
}
