package com.helen.api_crm.auth.dto;

import com.helen.api_crm.common.enums.Role;

public record CreateUserRequest(
        String email,
        String password,
        Role role
) {
}
