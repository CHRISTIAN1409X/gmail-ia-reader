package com.gmail.ia.reader.domain.dtos.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gmail.ia.reader.infraestructure.models.Role;


import java.util.Set;

public record UserRequestDto(
        Long id,
        Boolean isActive,
        Set<Role> roles,
        String name,
        String username,
        String email,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password
) {
}
