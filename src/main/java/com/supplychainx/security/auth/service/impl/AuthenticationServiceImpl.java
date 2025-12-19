package com.supplychainx.security.auth.service.impl;



import com.supplychainx.security.auth.dto.request.AuthenticationRequest;
import com.supplychainx.security.auth.dto.request.RegisterRequest;
import com.supplychainx.security.auth.dto.response.AuthenticationResponse;
import com.supplychainx.security.auth.service.AuthenticationService;
import com.supplychainx.supply_service.model.User;
import com.supplychainx.security.model.RefreshToken; // Note: Use your new location
import com.supplychainx.supply_service.repository.UserRepository;
import com.supplychainx.security.repository.RefreshTokenRepository; // Note: Use your new location
import com.supplychainx.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


    import java.util.List;

    @Service
    @RequiredArgsConstructor
    public class AuthenticationServiceImpl implements AuthenticationService {

        private final UserRepository repository;
        private final RefreshTokenRepository tokenRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        @Override
        public AuthenticationResponse register(RegisterRequest request) {
            // 1. Create User object
            User user = User.builder()
                    .firstName(request.getFirstname())
                    .lastName(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword())) // Hash password
                    .role(request.getRole())
                    .build();

            // 2. Save to DB
            User savedUser = repository.save(user);

            // 3. Generate Tokens
            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // 4. Save Refresh Token
            saveUserToken(savedUser, refreshToken);

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        }

        @Override
        public AuthenticationResponse authenticate(AuthenticationRequest request) {
            // 1. Validate Email/Password (Throws exception if invalid)
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // 2. Fetch User
            User user = repository.findByEmail(request.getEmail())
                    .orElseThrow();

            // 3. Generate NEW Tokens
            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // 4. Revoke OLD Tokens (Rotation)
            revokeAllUserTokens(user);

            // 5. Save NEW Refresh Token
            saveUserToken(user, refreshToken);

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        }

        // --- Helper Methods ---

        private void saveUserToken(User user, String jwtToken) {
            RefreshToken token = RefreshToken.builder()
                    .user(user)
                    .token(jwtToken)
                    .revoked(false)
                    .expiryDate(java.time.Instant.now().plusMillis(604800000)) // 7 days
                    .build();
            tokenRepository.save(token);
        }

        private void revokeAllUserTokens(User user) {
            List<RefreshToken> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
            if (validUserTokens.isEmpty())
                return;

            validUserTokens.forEach(token -> {
                token.setRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }
    }