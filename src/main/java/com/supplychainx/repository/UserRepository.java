package com.supplychainx.repository;

import com.supplychainx.model.User;
import com.supplychainx.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by their email (for login or validation)
    Optional<User> findByEmail(String email);

    // Check if a user with the given email already exists
    boolean existsByEmail(String email);

    // Find all users with a specific role (e.g., PROCUREMENT_MANAGER)
    List<User> findByRole(UserRole role);
}
