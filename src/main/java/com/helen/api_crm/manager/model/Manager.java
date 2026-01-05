package com.helen.api_crm.manager.model;

import com.helen.api_crm.auth.model.User;
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
@Table(name = "managers")
public class Manager extends User{

    @Column(nullable = false)
    private String name;
}
