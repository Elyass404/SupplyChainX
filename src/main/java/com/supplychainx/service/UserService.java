package com.supplychainx.service;

import com.supplychainx.dto.request.UserRequest;
import com.supplychainx.dto.response.UserResponse;
import com.supplychainx.model.enums.UserRole;
import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
    List<UserResponse> getUsersByRole(UserRole role);
}