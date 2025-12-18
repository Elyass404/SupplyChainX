package com.supplychainx.security.config;

import com.supplychainx.security.jwt.JwtAuthenticationFilter;
import com.supplychainx.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // Lombok generates the constructor for all 'final' fields (Dependency Injection)
public class SecurityConfig {

    // 1. Inject the JWT Filter we created
    // This is the "Guard" that checks the token header.
    private final JwtAuthenticationFilter jwtAuthFilter;

    // 2. Inject our custom User Service
    // This connects Security to your Database (UserRepository).
    private final UserDetailsServiceImpl userDetailsService;

    // 3. Inject the PasswordEncoder
    // Spring will automatically find the Bean you created in your 'PasswordConfig' file.
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // DISABLE CSRF
                // Best Practice: For stateless APIs (JWT), we disable CSRF because we don't use cookies/sessions.
                .csrf(AbstractHttpConfigurer::disable)

                // DEFINE AUTHORIZATION RULES
                .authorizeHttpRequests(auth -> auth
                        // A. Public Endpoints (WhiteList)
                        // We MUST allow access to Login/Register without a token, otherwise no one can enter!
                        // We will create these endpoints in the next step.
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // B. Secured Endpoints (Your existing rules)
                        .requestMatchers("/api/v1/raw-materials/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/suppliers/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                        .requestMatchers("/api/v1/supply-orders/**").hasAnyRole("ADMIN", "PURCHASING_MANAGER")

                        // C. Catch-All
                        // Any other request requires a valid JWT token.
                        .anyRequest().authenticated()
                )

                // STATELESS SESSION POLICY (Crucial for JWT)
                // This tells Spring: "Do NOT create a JSESSIONID cookie."
                // "Do NOT save the user in memory after the request finishes."
                // This satisfies the "API Stateless" requirement in your brief.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // AUTHENTICATION PROVIDER
                // Connects the "Who are you?" logic (DB) with the "Password check" logic (BCrypt).
                .authenticationProvider(authenticationProvider())

                // ADD THE JWT FILTER
                // We insert our JWT filter BEFORE the standard UsernamePasswordAuthenticationFilter.
                // Why? We want to check the token *first*. If it's valid, we log them in immediately.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * AUTHENTICATION PROVIDER
     * This Bean tells Spring Security:
     * 1. Where to find users (userDetailsService)
     * 2. How to check passwords (passwordEncoder)
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * AUTHENTICATION MANAGER
     * We need to export this Bean so we can use it in our LoginController later.
     * This is the component that actually performs the `authManager.authenticate(...)` call.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}