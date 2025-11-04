package com.supplychainx.controller;

import com.supplychainx.dto.request.UserRequest;
import com.supplychainx.dto.response.UserResponse;
import com.supplychainx.model.enums.UserRole;
import com.supplychainx.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // --- 1. CREATE User (POST) ---
    // Endpoint: POST /api/v1/users
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.createUser(request);

        // Build the URI for the Location header (Best practice for 201 Created)
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        // Returns 201 Created
        return ResponseEntity.created(location).body(response);
    }

    // --- 2. RETRIEVE All Users (GET) ---
    // Endpoint: GET /api/v1/users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // --- 3. RETRIEVE User by ID (GET) ---
    // Endpoint: GET /api/v1/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // --- 4. Query: Users by Role (GET) ---
    // Endpoint: GET /api/v1/users/by-role?role=ADMIN
    @GetMapping("/by-role")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@RequestParam UserRole role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    // --- 5. UPDATE User (PUT) ---
    // Endpoint: PUT /api/v1/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    // --- 6. DELETE User (DELETE) ---
    // Endpoint: DELETE /api/v1/users/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Returns 204 No Content
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}