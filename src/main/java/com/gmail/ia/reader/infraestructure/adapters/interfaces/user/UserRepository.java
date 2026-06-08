package com.gmail.ia.reader.infraestructure.adapters.interfaces.user;

import com.gmail.ia.reader.infraestructure.models.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String identification);
}
