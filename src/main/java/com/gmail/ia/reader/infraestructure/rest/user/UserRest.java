package com.gmail.ia.reader.infraestructure.rest.user;


import com.gmail.ia.reader.application.usecases.user.UserService;
import com.gmail.ia.reader.domain.dtos.user.UserRequestDto;
import com.gmail.ia.reader.domain.dtos.user.UserResponseDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/user")
public class UserRest {
    private final UserService userService;

    public UserRest(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestBody UserRequestDto userRequestDto){
        userService.create(userRequestDto, userService::convertToDto);
        return "OK";
    }


    @GetMapping("/list")
    public List<UserResponseDto> userRequestDtoList(){
        System.out.println("LISTA USUARIOS");
        return userService.getUserList();
    }


    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable Long id){
        return userService.search(id);
    }

    @PutMapping("/")
    public UserResponseDto updateUser(@RequestBody UserRequestDto userRequestDto){
        return userService.update(userRequestDto);
    }

    @DeleteMapping("/{id}")
    public UserResponseDto deleteUser(@PathVariable Long id){
        return null;
    }

}
