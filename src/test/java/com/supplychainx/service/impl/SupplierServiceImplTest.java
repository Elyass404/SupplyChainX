package com.supplychainx.service.impl;
import com.supplychainx.supply_service.dto.response.SupplierResponse;
import com.supplychainx.supply_service.mapper.SupplierMapper;
import com.supplychainx.supply_service.model.Supplier;
import com.supplychainx.supply_service.repository.SupplierRepository;
import com.supplychainx.supply_service.service.impl.SupplierServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SupplierServiceImplTest{

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    @Test
    void testGetAllSuppliers(){
        Supplier supplier = Supplier.builder()
                .id(1L)
                .name("ilyass company")
                .email("ilyass@company.com")
                .rating(4.9)
                .leadTime(22)
                .build();

        SupplierResponse supplierResponse = SupplierResponse.builder().
                id(1L)
                .name("ilyass company")
                .email("ilyass@company.com")
                .rating(4.9)
                .leadTime(22)
                .build();

        List<Supplier> expectedSuppliersEntities = new ArrayList<>();
        expectedSuppliersEntities.add(supplier);

        List<SupplierResponse> expectedSuppliersResponse = new ArrayList<>();
        expectedSuppliersResponse.add(supplierResponse);

        when(supplierRepository.findAll()).thenReturn(expectedSuppliersEntities);

        when(supplierMapper.toResponseList(expectedSuppliersEntities)).thenReturn(expectedSuppliersResponse);

        List<SupplierResponse> actualSupplierResponse = supplierService.getAllSuppliers();

        assertEquals(1, actualSupplierResponse.size(), "The size of the list should be 1.");
        assertEquals("ilyass company", actualSupplierResponse.get(0).getName(), "The name should be: ilyass company.");
        assertEquals(4.9, actualSupplierResponse.get(0).getRating(), "The rating should be 4.9");

        verify(supplierRepository, times(1)).findAll();
        verify(supplierMapper, times(1)).toResponseList(expectedSuppliersEntities);

    }


}