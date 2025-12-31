package com.supplychainx.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor // Creates a constructor for final fields (Injection)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    // We need this to load the full User object from DB to check roles, etc.
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Get the Auth Header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Check if Header is missing or doesn't start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Pass the request down the chain
            return; // Don't continue logic here
        }

        // 3. Extract the Token
        jwt = authHeader.substring(7); // "Bearer " is 7 chars
        userEmail = jwtService.extractUsername(jwt);

        // 4. If User is present in token but NOT authenticated yet in this context
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details from DB
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 5. Validate Token
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 6. Create Authentication Token (The "Security Ticket")
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // No credentials needed (we already verified the token)
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 7. Update Security Context (Log the user in)
                SecurityContextHolder.getContext().setAuthentication(authToken);

                //now iam adding the details to the log, so we know who made the action and what whose the log is related to
                MDC.put("userId", userEmail);
                MDC.put("userRole", userDetails.getAuthorities().toString());
            }
        }

        // 8. Continue the chain (go to the next filter or the Controller)
        try{
            filterChain.doFilter(request, response);
        }finally {
            // If we don't do this, the next user might inherit the previous user's ID!
            MDC.clear();
        }
    }
}