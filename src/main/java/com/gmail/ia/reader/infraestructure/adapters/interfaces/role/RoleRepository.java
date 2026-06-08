package com.gmail.ia.reader.infraestructure.adapters.interfaces.role;

import com.gmail.ia.reader.infraestructure.models.Role;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findRoleByName(String name);
}
