package com.gmail.ia.reader.application.usecases.role;

import com.gmail.ia.reader.infraestructure.models.Role;

import java.util.Optional;

public interface RoleServiceBasicOperations {
    Optional<Role> findRoleByName(String nameRole);
}
