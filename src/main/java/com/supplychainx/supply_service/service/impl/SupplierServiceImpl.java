package com.supplychainx.supply_service.service.impl;

import com.supplychainx.supply_service.dto.request.SupplierRequest;
import com.supplychainx.supply_service.dto.response.SupplierResponse;
import com.supplychainx.exception.NameAlreadyExistsException;
import com.supplychainx.exception.RessourceNotFoundException;
import com.supplychainx.supply_service.mapper.SupplierMapper;
import com.supplychainx.supply_service.model.Supplier;
import com.supplychainx.supply_service.repository.SupplierRepository;
import com.supplychainx.supply_service.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    // --- CRUD Operations ---

    @Override
    public SupplierResponse createSupplier(SupplierRequest request) {
        // 1. Check for Name conflict
        if (supplierRepository.existsByName(request.getName())) {
            throw new NameAlreadyExistsException("Supplier with name '" + request.getName() + "' already exists.");
        }

        // 2. Check for Email conflict
        if (supplierRepository.existsByEmail(request.getEmail())) {
            throw new NameAlreadyExistsException("Supplier with email '" + request.getEmail() + "' already exists.");
        }

        // 3. Convert DTO to Entity
        Supplier newSupplier = supplierMapper.toEntity(request);

        // 4. Save Entity
        Supplier savedSupplier = supplierRepository.save(newSupplier);

        // 5. Convert saved Entity to Response DTO
        return supplierMapper.toResponse(savedSupplier);
    }

    @Override
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Supplier not found with ID: " + id));

        // 1. Check for Name conflict during update
        if (!existingSupplier.getName().equalsIgnoreCase(request.getName()) &&
                supplierRepository.existsByName(request.getName())) {
            throw new NameAlreadyExistsException("Cannot update. Supplier with name '" + request.getName() + "' already exists.");
        }

        // 2. Check for Email conflict during update
        if (!existingSupplier.getEmail().equalsIgnoreCase(request.getEmail()) &&
                supplierRepository.existsByEmail(request.getEmail())) {
            throw new NameAlreadyExistsException("Cannot update. Supplier with email '" + request.getEmail() + "' already exists.");
        }

        // 3. Apply changes from DTO to existing Entity
        existingSupplier.setName(request.getName());
        existingSupplier.setEmail(request.getEmail());
        existingSupplier.setRating(request.getRating());
        existingSupplier.setLeadTime(request.getLeadTime());

        // 4. Save and return Response DTO
        Supplier savedSupplier = supplierRepository.save(existingSupplier);
        return supplierMapper.toResponse(savedSupplier);
    }

    @Override
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RessourceNotFoundException("Supplier not found with ID: " + id);
        }
        supplierRepository.deleteById(id);
    }

    // --- Retrieval & Utility Methods ---

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(Long id) {

        return supplierRepository.findById(id)
                .map(supplierMapper::toResponse) // Map if present
                .orElseThrow(() -> new RessourceNotFoundException("Supplier not found with ID: " + id)); // Throw if not present
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getAllSuppliers() {
        return supplierMapper.toResponseList(supplierRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplierResponse> getSupplierByName(String name) {
        // Correct use of Optional: returns Optional.empty() if not found.
        return supplierRepository.findByName(name)
                .map(supplierMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return supplierRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return supplierRepository.existsByEmail(email);
    }

    // --- Business Methods ---

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> searchSuppliers(String query) {
        return supplierMapper.toResponseList(
                supplierRepository.findByNameContainingIgnoreCase(query)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getHighRatedSuppliers(Double minRating) {
        return supplierMapper.toResponseList(
                supplierRepository.findByRatingGreaterThanEqual(minRating)
        );
    }
}