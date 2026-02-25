package com.ntt.realestate.controller;

import com.ntt.realestate.dto.LoginRequest;
import com.ntt.realestate.dto.LoginResponse;
import com.ntt.realestate.model.User;
import com.ntt.realestate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);

        // Check credentials
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.ok(LoginResponse.builder()
                .success(false)
                .errorCode("MSG-LOGIN-INFOR-ERROR")
                .build());
        }

        // Check account status
        if (!"active".equals(user.getStatus())) {
            return ResponseEntity.ok(LoginResponse.builder()
                .success(false)
                .errorCode("MSG-LOGIN-INFOR-NOT-ACTIVE")
                .build());
        }

        return ResponseEntity.ok(LoginResponse.builder()
            .success(true)
            .displayName(user.getDisplayName())
            .build());
    }
}
