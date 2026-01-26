package com.helen.api_crm.security.model;

import com.helen.api_crm.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class SecurityUser implements UserDetails {

    private final Long id;
    private final String email;
    private final Role role;
    private final Collection<? extends GrantedAuthority> authorities;

    public SecurityUser(Long id, String email, String roleString) {
        this.id = id;
        this.email = email;
        this.role = Role.valueOf(roleString);
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + roleString));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    @Override
    public String getPassword() {
        return null;
    }
    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
