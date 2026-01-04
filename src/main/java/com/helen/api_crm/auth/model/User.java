package com.helen.api_crm.auth.model;

import com.helen.api_crm.common.enums.Role;
import com.helen.api_crm.manager.model.Manager;
import com.helen.api_crm.seller.model.Seller;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean active = true;

    @OneToOne(mappedBy = "user")
    private Seller seller;

    @OneToOne(mappedBy = "user")
    private Manager manager;

}
