package com.supplychainx.security.auth.dto.request;

import com.supplychainx.supply_service.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {


    @NotBlank(message = "Please, you should insert your first name")
    private String firstname;

    @NotBlank(message = "Please insert your lastname, it is mandatory")
    private String lastname;

    @NotBlank(message = "Please insert your email")
    @Email(message= "Please make sure you insert a valid email")
    private String email;

    @NotBlank(message = "Please, make sure you provide a password so you can access your profile")
    private String password;

    @NotNull(message = "You forgot providing the the role")
    private UserRole role ;
}
