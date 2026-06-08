package com.gmail.ia.reader.application.usecases.user;


import com.gmail.ia.reader.domain.dtos.user.UserRequestDto;
import com.gmail.ia.reader.domain.dtos.user.UserResponseDto;
import com.gmail.ia.reader.infraestructure.models.User;

import java.util.List;
import java.util.function.Function;

public interface UserService {
    UserResponseDto search(Long id);
    <R> R create(UserRequestDto object, Function<User,R> function);
    UserResponseDto update(UserRequestDto object);
    void activeDelete(Long id);
    UserResponseDto convertToDto(User user);
    List<UserResponseDto> getUserList();
    <R> R converTo(User simpleUserDto, Function<User,R> function);
}
