package com.supplychainx.supply_service.service.impl;

import com.supplychainx.supply_service.dto.request.UserRequest;
import com.supplychainx.supply_service.dto.response.UserResponse;
import com.supplychainx.exception.RessourceNotFoundException; // Ensure this exception exists
import com.supplychainx.supply_service.mapper.UserMapper; // Ensure this mapper is ready
import com.supplychainx.supply_service.model.User;
import com.supplychainx.supply_service.model.enums.UserRole;
import com.supplychainx.supply_service.repository.UserRepository; // Ensure this repository is ready
import com.supplychainx.supply_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // Use this utility interface
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    // Inject the utility configured in PasswordConfig
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists.");
        }

        User newUser = userMapper.toEntity(request);

        // **Hashing Step**: Use the utility to hash the password
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(newUser);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("User not found with ID: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userMapper.toResponseList(userRepository.findAll());
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("User not found with ID: " + id));

        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setRole(request.getRole());

        // Update password only if a new one is provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            // **Hashing Step**: Use the utility to hash the new password
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RessourceNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(UserRole role) {
        List<User> users = userRepository.findByRole(role);
        return userMapper.toResponseList(users);
    }
}