package com.supplychainx.supply_service.service.impl;

import com.supplychainx.supply_service.dto.request.SupplierRequest; // Import added
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
import static org.junit.jupiter.api.Assertions.assertNotNull; // Import added
import static org.mockito.ArgumentMatchers.any; // Import added
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    @Test
    void testGetAllSuppliers() {
        // --- ARRANGE ---
        Supplier supplier = Supplier.builder()
                .id(1L)
                .name("ilyass company")
                .email("ilyass@company.com")
                .rating(4.9)
                .leadTime(22)
                .build();

        SupplierResponse supplierResponse = SupplierResponse.builder()
                .id(1L)
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

        // --- ACT ---
        List<SupplierResponse> actualSupplierResponse = supplierService.getAllSuppliers();

        // --- ASSERT ---
        assertEquals(1, actualSupplierResponse.size(), "The size of the list should be 1.");
        assertEquals("ilyass company", actualSupplierResponse.get(0).getName(), "The name should be: ilyass company.");
        assertEquals(4.9, actualSupplierResponse.get(0).getRating(), "The rating should be 4.9");

        verify(supplierRepository, times(1)).findAll();
        verify(supplierMapper, times(1)).toResponseList(expectedSuppliersEntities);
    }

    // --- another test, i will explain here in order to make it easier for me to re documentate about unit test ---

    @Test
    void testCreateSupplier() {
        // 1. ARRANGE: Prepare the data
        // The input DTO coming from the Controller
        SupplierRequest request = SupplierRequest.builder()
                .name("New Tech")
                .email("contact@newtech.com")
                .rating(4.5)
                .leadTime(10)
                .build();

        // The Entity that the Mapper will create (No ID yet)
        Supplier supplierToSave = Supplier.builder()
                .name("New Tech")
                .email("contact@newtech.com")
                .rating(4.5)
                .leadTime(10)
                .build();

        // The Entity that the Repository returns after saving (Has ID now)
        Supplier savedSupplier = Supplier.builder()
                .id(50L) // like if the db gave it an id
                .name("New Tech")
                .email("contact@newtech.com")
                .rating(4.5)
                .leadTime(10)
                .build();

        // The Response DTO to return to the user
        SupplierResponse expectedResponse = SupplierResponse.builder()
                .id(50L)
                .name("New Tech")
                .email("contact@newtech.com")
                .rating(4.5)
                .leadTime(10)
                .build();

        // TEACH THE MOCKS
        when(supplierMapper.toEntity(request)).thenReturn(supplierToSave);
        // we use 'any(Supplier.class)' because the object instance passed to save might differ slightly in memory
        when(supplierRepository.save(any(Supplier.class))).thenReturn(savedSupplier);
        when(supplierMapper.toResponse(savedSupplier)).thenReturn(expectedResponse);

        // 2. ACT: Call the service method
        SupplierResponse actualResponse = supplierService.createSupplier(request);

        // 3. ASSERT: Check results
        assertNotNull(actualResponse);
        assertEquals(50L, actualResponse.getId());
        assertEquals("New Tech", actualResponse.getName());

        // Verify the interaction flow
        verify(supplierMapper).toEntity(request);
        verify(supplierRepository).save(any(Supplier.class)); // Ensure save was called
    }
}