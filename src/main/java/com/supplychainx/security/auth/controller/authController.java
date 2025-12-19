package com.supplychainx.security.auth.controller;

import com.supplychainx.security.auth.dto.request.AuthenticationRequest;
import com.supplychainx.security.auth.dto.request.RegisterRequest;
import com.supplychainx.security.auth.dto.response.AuthenticationResponse;
import com.supplychainx.security.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class authController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
       return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest resuest){
        return ResponseEntity.ok(authService.authenticate(resuest));
    }
}
