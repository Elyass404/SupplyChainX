package com.supplychainx.security.config;

import com.supplychainx.security.exception.CustomAccessDeniedHandler;
import com.supplychainx.security.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Map;
import java.util.List;
import java.util.Collection;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (Standard for APIs)
                .csrf(AbstractHttpConfigurer::disable)

                //Define URL Permissions
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll() // Keep error page open

                        .requestMatchers("/api/v1/raw-materials/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/suppliers/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                        .requestMatchers("/api/v1/supply-orders/**").hasAnyRole("ADMIN", "PURCHASING_MANAGER")
                        //i can add more endpoints here to secure them by roles

                        .anyRequest().authenticated()
                )

                // Exception Handling
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // For 401
                        .accessDeniedHandler(customAccessDeniedHandler)           // For 403
                )

                // 3. Enable Keycloak (OAuth2 Resource Server)
                // so it is like iam telling spring to "Validate the token against the URL in application.yml"
                .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                        .decoder(jwtDecoder()) // uses the custom decoder
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()) // uses the new converter
                )

                .authenticationEntryPoint(customAuthenticationEntryPoint)
        );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // 1. Where to download keys (Use Internal Docker Name)
        String jwkSetUri = "http://keycloak:8080/realms/supplychain-realm/protocol/openid-connect/certs";

        // 2. Who issued the token (Use External Name from Postman)
        String expectedIssuer = "http://localhost:8081/realms/supplychain-realm";

        // Build the decoder using the INTERNAL connection
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        // Add a validator to check the EXTERNAL issuer name
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(expectedIssuer);
        jwtDecoder.setJwtValidator(withIssuer);

        return jwtDecoder;
    }

    // to extract roles from Keycloak
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 1. Tell Spring to look for the "roles" inside the token
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        // 2. Add the "ROLE_" prefix (so "ADMIN" becomes "ROLE_ADMIN")
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // A. Standard scopes (optional)
            var authorities = grantedAuthoritiesConverter.convert(jwt);

            // B. Extract REALM ROLES from "realm_access"
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null) {
                List<String> roles = (List<String>) realmAccess.get("roles");
                if (roles != null) {
                    roles.forEach(role -> {
                        // Create the authority "ROLE_ADMIN"
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    });
                }
            }
            return authorities;
        });

        return jwtAuthenticationConverter;
    }
}