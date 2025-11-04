package com.supplychainx.dto.request;

import com.supplychainx.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class UserRequest {

    @NotBlank(message = "first name is required.")
    String firstName;

    @NotBlank(message = "last name is required.")
    String lastName;

    @NotBlank(message = "email is required ")
    @Email(message = "You should insert a valid email")
    String email;

    @NotBlank(message = "password is required.")
    String password;

    @NotNull(message = "Role is required.")
    UserRole role;

}
