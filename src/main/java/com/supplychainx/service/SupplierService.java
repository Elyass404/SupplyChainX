package com.supplychainx.service;

import com.supplychainx.dto.request.SupplierRequest;
import com.supplychainx.dto.response.SupplierResponse;

import java.util.List;
import java.util.Optional;

public interface SupplierService {

    // --- CRUD Operations ---
    SupplierResponse createSupplier(SupplierRequest request);

    SupplierResponse updateSupplier(Long id, SupplierRequest request);

    void deleteSupplier(Long id);

    // --- Retrieval ---

    // CHANGED: Now returns SupplierResponse directly, throwing RessourceNotFoundException if not found.
    SupplierResponse getSupplierById(Long id);

    List<SupplierResponse> getAllSuppliers();

    Optional<SupplierResponse> getSupplierByName(String name);

    // --- Business & Utility Methods ---

    boolean existsByName(String name);

    boolean existsByEmail(String email);

    List<SupplierResponse> searchSuppliers(String query);

    List<SupplierResponse> getHighRatedSuppliers(Double minRating);
}