package com.supplychainx.service;

import com.supplychainx.model.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierService {

    Supplier createSupplier(Supplier  supplier);

    Supplier updateSupplier(Long id,Supplier supplier);

    void deleteSupplier(Long id);

    Optional<Supplier> getSupplierById(Long id);

    List<Supplier> getAllSuppliers();

    boolean existsByEmail(String email);
}
