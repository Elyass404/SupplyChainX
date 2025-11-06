package com.supplychainx.supply_service.service;

import com.supplychainx.supply_service.dto.request.UserRequest;
import com.supplychainx.supply_service.dto.response.UserResponse;
import com.supplychainx.supply_service.model.enums.UserRole;
import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
    List<UserResponse> getUsersByRole(UserRole role);
}