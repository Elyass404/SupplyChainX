package com.supplychainx.dto.response;
//this is a simplified dto response for the raw material to be used in the production module
import lombok.Value;

@Value
public class RawMaterialSimpleResponse {
    Long id;
    String name;
    String unit ;
}
