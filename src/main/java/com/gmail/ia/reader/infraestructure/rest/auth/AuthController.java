package com.gmail.ia.reader.infraestructure.rest.auth;

import com.gmail.ia.reader.infraestructure.custom.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/verify")
    public ResponseEntity<?> verifySession(Authentication authentication) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(customUserDetails.getAuthUser());
    }
}
