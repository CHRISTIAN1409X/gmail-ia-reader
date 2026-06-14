package com.gmail.ia.reader.infraestructure.adapters.implementation.email;

import com.gmail.ia.reader.global.domain.ports.DaoCrudPort;
import com.gmail.ia.reader.infraestructure.models.Email;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class EmailAdapter implements DaoCrudPort<Email> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Email> selectAll() {
        return List.of();
    }

    @Override
    public Optional<Email> get(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Email> create(Email object) {
        entityManager.persist(object);
        return Optional.of(object);
    }

    @Override
    public Optional<Email> update(Email object) {
        return Optional.empty();
    }

    @Override
    public Optional<Email> delete(Long id) {
        return Optional.empty();
    }
}
