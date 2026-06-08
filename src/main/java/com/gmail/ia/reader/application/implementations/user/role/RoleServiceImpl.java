package com.gmail.ia.reader.application.implementations.user.role;


import com.gmail.ia.reader.application.usecases.user.role.RoleServiceBasicOperations;
import com.gmail.ia.reader.infraestructure.adapters.interfaces.role.RoleRepository;
import com.gmail.ia.reader.infraestructure.models.Role;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleServiceBasicOperations {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findRoleByName(String nameRole) {
        return roleRepository.findRoleByName(nameRole);
    }
}
