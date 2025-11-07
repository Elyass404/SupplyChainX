package com.supplychainx.delivery_service.dto.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class CustomerResponse {

    Long id;
    String name;
    String city;
    String address;

    //i personally added this to know if the customer has an active order
    //so i dont delete the active
    Integer activeOrderCount;


}
