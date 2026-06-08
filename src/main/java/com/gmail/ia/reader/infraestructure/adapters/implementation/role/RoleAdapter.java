package com.gmail.ia.reader.infraestructure.adapters.implementation.role;

import com.gmail.ia.reader.infraestructure.adapters.interfaces.role.RoleRepository;
import com.gmail.ia.reader.infraestructure.models.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RoleAdapter implements RoleRepository {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Optional<Role> findRoleByName(String name) {
        return entityManager
                .createQuery("select r from Role r where r.name= :name", Role.class)
                .setParameter("name",name)
                .getResultStream()
                .findFirst();
    }
}
