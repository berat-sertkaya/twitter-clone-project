package com.workintech.twitter.controller;

import com.workintech.twitter.dto.RegisterRequest;
import com.workintech.twitter.dto.UserResponse;
import com.workintech.twitter.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping({"/register", "/register/"})
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    // HTTP Basic ile kimlik dogrulanir; basarili ise kullanici bilgisi doner
    @PostMapping({"/login", "/login/"})
    public UserResponse login(Authentication authentication) {
        return userService.me(authentication.getName());
    }
}
