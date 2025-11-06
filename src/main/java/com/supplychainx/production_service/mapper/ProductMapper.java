package com.supplychainx.production_service.mapper;

import com.supplychainx.production_service.dto.request.ProductRequest;
import com.supplychainx.production_service.dto.response.ProductResponse;
import com.supplychainx.production_service.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel="spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductRequest request);

    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);
}
