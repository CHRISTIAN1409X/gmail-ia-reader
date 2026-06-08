package com.gmail.ia.reader.infraestructure.custom;

import com.gmail.ia.reader.domain.dtos.user.AuthUser;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {

    private final AuthUser authUser;

    public CustomUserDetails(String email, String password, boolean enabled, boolean accountNoExpired,
                             boolean credentialsNonExpired, boolean accountNonLocked,
                             List<GrantedAuthority> authorities, AuthUser authUser){
        super(email,password,enabled,true,true,true,authorities);
        this.authUser = authUser;
    }

    public AuthUser getAuthUser(){
        return this.authUser;
    }

}