package com.gmail.ia.reader.infraestructure.adapters.implementation.user;

import com.gmail.ia.reader.global.domain.ports.DaoCrudPort;
import com.gmail.ia.reader.infraestructure.adapters.interfaces.user.UserRepository;
import com.gmail.ia.reader.infraestructure.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserAdapter implements DaoCrudPort<User>,UserRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Optional<User> findByEmail(String identification) {
        try {
            User user = entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", identification)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> selectAll() {
        return List.of();
    }

    @Override
    public Optional<User> get(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> create(User object) {
        entityManager.persist(object);
        return Optional.of(object);
    }

    @Override
    public Optional<User> update(User object) {
        return Optional.empty();
    }

    @Override
    public Optional<User> delete(Long id) {
        return Optional.empty();
    }
}
