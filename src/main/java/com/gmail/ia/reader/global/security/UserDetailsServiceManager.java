package com.gmail.ia.reader.global.security;


import com.gmail.ia.reader.domain.dtos.user.AuthUser;
import com.gmail.ia.reader.infraestructure.adapters.interfaces.user.UserRepository;
import com.gmail.ia.reader.infraestructure.custom.CustomUserDetails;
import com.gmail.ia.reader.infraestructure.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceManager implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceManager(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(String.format("email - %s -passed not exists in the DATABASE",email)));


        List<GrantedAuthority> grantedAuthorityList = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new CustomUserDetails(user.getEmail(),
                user.getPasswordHash(),
                user.getIsActive(),
                true,
                true,
                true,
                grantedAuthorityList,
                new AuthUser(user.getUsername()));
    }
}
