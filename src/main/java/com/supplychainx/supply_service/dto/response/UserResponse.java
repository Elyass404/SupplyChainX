package com.supplychainx.supply_service.dto.response;

import com.supplychainx.supply_service.model.enums.UserRole;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserResponse {

    Long id;

    String firstName;

    String lastName;

    String email;

    UserRole role;
}
