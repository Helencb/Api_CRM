package com.helen.api_crm.manager.model;

import com.helen.api_crm.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "managers")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
