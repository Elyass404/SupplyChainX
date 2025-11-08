package com.supplychainx.delivery_service.dto.request;

import com.supplychainx.delivery_service.model.Customer;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderRequest {

    @NotNull(message = "The customer ID is required.")
    Long customerId;

    @NotNull(message = "The product Id is required.")
    Long productId;

    @NotNull(message = "The quantity is required")
    @Min(value = 1 , message = "The quantity should be at least 1.")
    Integer quantity;


    //for the status that will be handeled in the service to give automatically the new orders the value of in Preparation




}
