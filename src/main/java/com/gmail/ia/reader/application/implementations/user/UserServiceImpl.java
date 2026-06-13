package com.gmail.ia.reader.application.implementations.user;

import com.gmail.ia.reader.application.usecases.user.UserService;
import com.gmail.ia.reader.application.usecases.user.UserServiceBasicOperations;
import com.gmail.ia.reader.application.usecases.role.RoleServiceBasicOperations;
import com.gmail.ia.reader.domain.dtos.user.UserRequestDto;
import com.gmail.ia.reader.domain.dtos.user.UserResponseDto;
import com.gmail.ia.reader.global.domain.ports.DaoCrudPort;
import com.gmail.ia.reader.infraestructure.adapters.interfaces.role.RoleRepository;
import com.gmail.ia.reader.infraestructure.adapters.interfaces.user.UserRepository;

import com.gmail.ia.reader.infraestructure.advicers.exceptions.ResourceAlreadyExists;
import com.gmail.ia.reader.infraestructure.models.Role;
import com.gmail.ia.reader.infraestructure.models.User;
import com.gmail.ia.reader.utils.ThrowableActions;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserServiceBasicOperations {



    private final RoleServiceBasicOperations roleServiceBasicOperations;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final DaoCrudPort<User> userDaoCrudPort;


    public UserServiceImpl(RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           UserRepository userRepository,
                           DaoCrudPort<User> userDaoCrudPort,
                           RoleServiceBasicOperations roleServiceBasicOperations){
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userDaoCrudPort = userDaoCrudPort;
        this.roleServiceBasicOperations = roleServiceBasicOperations;
    }

    @Override
    public UserResponseDto search(Long id) {
        return findUserById(id)
                .map(this::convertToDto)
                .orElseThrow();
    }

    @Override
    @Transactional
    public <R> R create(UserRequestDto userRequestDto, Function<User,R> function) {
        User user;
        Set<Role> roles;
        Boolean isAdmin = false;
        String passwordEncoded = "";
        boolean isActive = true;
        Optional.ofNullable(userRequestDto.email())
                .flatMap(this::findUserByEmail)
                .ifPresent((userOpt)->
                        ThrowableActions.launchRuntimeExeption(
                                ()-> new ResourceAlreadyExists("Error user with email: "+"already exits")
                        ));

        try {
            roles = userRequestDto.roles()
                    .stream()
                    .map(role -> roleRepository.findRoleByName(role.getName()))
                    .filter(Optional::isPresent)
                    .map(Optional::orElseThrow)
                    .collect(Collectors.toSet());
            if (userRequestDto.isActive()==null || userRequestDto.isActive()){
                passwordEncoded = passwordEncoder.encode(userRequestDto.password());
            }

            user = User.builder()
                    .name(userRequestDto.name())
                    .username(userRequestDto.username())
                    .email(userRequestDto.email())
                    .passwordHash(passwordEncoded)
                    .roles(roles)
                    .isAdmin(isAdmin)
                    .isActive(isActive)
                    .build();

            System.out.println(user);
            User userCreated =  Optional.ofNullable(create(user)).orElseThrow();
            return function.apply(userCreated);
        }catch (Exception e){
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public UserResponseDto update(UserRequestDto object) {
        User user = new User();
        user.setName(object.name());
        user.setPasswordHash(object.password());
        user.setUsername(object.username());
        user.setRoles(object.roles());

        return  Optional.ofNullable(update(user))
                .map(this::convertToDto)
                .orElseThrow();
    }






    @Override
    public void activeDelete(Long id) {

    }


    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(objUser-> (User) objUser);
    }

    public Optional<User> findUserById(Long id){
        return userDaoCrudPort.get(id);
    }

    @Override
    public User update(User user) {
        return userDaoCrudPort.update(user)
                .orElseThrow();
    }

    @Override
    public User create(User user) {
        return userDaoCrudPort.create(user)
                .orElseThrow();
    }

    @Override
    public UserResponseDto convertToDto(User user) {
        UserRequestDto userRequestDto = new UserRequestDto(
                null,
                user.getIsActive(),
                user.getRoles(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                ""
        );
        return new UserResponseDto(userRequestDto);
    }

    @Override
    public List<UserResponseDto> getUserList() {
        return List.of();
    }

    @Override
    public <R> R converTo(User user, Function<User, R> function) {
        return function.apply(user);
    }
}
