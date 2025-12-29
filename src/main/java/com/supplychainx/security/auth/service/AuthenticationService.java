package com.supplychainx.security.auth.service;

import com.supplychainx.security.auth.dto.request.AuthenticationRequest;
import com.supplychainx.security.auth.dto.request.RefreshTokenRequest;
import com.supplychainx.security.auth.dto.request.RegisterRequest;
import com.supplychainx.security.auth.dto.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);

    AuthenticationResponse refreshToken(RefreshTokenRequest request);
}
