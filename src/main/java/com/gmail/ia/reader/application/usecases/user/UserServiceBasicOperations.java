package com.gmail.ia.reader.application.usecases.user;



import com.gmail.ia.reader.infraestructure.models.User;

import java.util.Optional;

public interface UserServiceBasicOperations {
    Optional<User> findUserByEmail(String email);

    User update(User user);

    User create(User user);

    Optional<User> findUserById(Long id);
}
