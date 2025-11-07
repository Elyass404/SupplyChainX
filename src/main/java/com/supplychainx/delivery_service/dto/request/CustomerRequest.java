package com.supplychainx.delivery_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CustomerRequest {

    @NotBlank(message = "The name is required.")
    String name;

    @NotBlank(message = "The address is required.")
    String address;

    @NotBlank(message = "The city is required.")
    String city;

}
