package com.gmail.ia.reader.application.usecases.user.role;

import com.gmail.ia.reader.infraestructure.models.Role;

import java.util.Optional;

public interface RoleServiceBasicOperations {
    Optional<Role> findRoleByName(String nameRole);
}
