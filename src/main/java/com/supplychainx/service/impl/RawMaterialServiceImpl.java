package com.supplychainx.service.impl;

import com.supplychainx.dto.request.RawMaterialRequest;
import com.supplychainx.dto.response.RawMaterialResponse;
import com.supplychainx.exception.NameAlreadyExistsException;
import com.supplychainx.exception.RessourceNotFoundException;
import com.supplychainx.mapper.RawMaterialMapper; // New import
import com.supplychainx.model.RawMaterial;
import com.supplychainx.model.Supplier;
import com.supplychainx.repository.RawMaterialRepository;
import com.supplychainx.repository.SupplierRepository; // New import
import com.supplychainx.service.RawMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RawMaterialServiceImpl implements RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;
    private final SupplierRepository supplierRepository; // Injected to handle relationships
    private final RawMaterialMapper rawMaterialMapper; // Injected Mapper

    // --- Helper Method for Suppliers ---
    /**
     * Finds and returns a list of Supplier entities based on their IDs.
     * Throws RessourceNotFoundException if any ID is invalid.
     */
    private List<Supplier> findSuppliersByIds(List<Long> supplierIds) {
        if (supplierIds == null || supplierIds.isEmpty()) {
            throw new IllegalArgumentException("Raw material must be linked to at least one supplier.");
        }

        List<Supplier> suppliers = supplierRepository.findAllById(supplierIds);

        if (suppliers.size() != supplierIds.size()) {
            // This is a robust way to check if all IDs were found
            throw new RessourceNotFoundException("One or more supplier IDs provided were invalid.");
        }
        return suppliers;
    }

    // --- CRUD Operations ---

    @Override
    public RawMaterialResponse createRawMaterial(RawMaterialRequest request) {
        // 1. Uniqueness check
        if (rawMaterialRepository.existsByName(request.getName())) {
            throw new NameAlreadyExistsException("Material with the Name: " + request.getName() + " already exists.");
        }

        // 2. Fetch related entities (Suppliers)
        List<Supplier> suppliers = findSuppliersByIds(request.getSupplierIds());

        // 3. Convert DTO to Entity and set relationships
        RawMaterial newMaterial = rawMaterialMapper.toEntity(request);
        newMaterial.setSuppliers(suppliers);

        // 4. Save and return Response DTO
        RawMaterial savedMaterial = rawMaterialRepository.save(newMaterial);
        return rawMaterialMapper.toResponse(savedMaterial);
    }

    @Override
    public RawMaterialResponse updateRawMaterial(Long id, RawMaterialRequest request) {
        RawMaterial existingRawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Material with ID: " + id + " not found."));

        // 1. Uniqueness check for name change
        if (!existingRawMaterial.getName().equalsIgnoreCase(request.getName()) &&
                rawMaterialRepository.existsByName(request.getName())) {
            throw new NameAlreadyExistsException("Cannot update. Material with name '" + request.getName() + "' already exists.");
        }

        // 2. Fetch related entities (Suppliers)
        List<Supplier> suppliers = findSuppliersByIds(request.getSupplierIds());

        // 3. Update core fields and relationships
        existingRawMaterial.setName(request.getName());
        existingRawMaterial.setStock(request.getStock());
        existingRawMaterial.setStockMin(request.getStockMin());
        existingRawMaterial.setUnit(request.getUnit());
        existingRawMaterial.setSuppliers(suppliers); // Update the relationship

        // 4. Save and return Response DTO
        RawMaterial savedMaterial = rawMaterialRepository.save(existingRawMaterial);
        return rawMaterialMapper.toResponse(savedMaterial);
    }

    @Override
    public void deleteRawMaterial(Long id) {
        if (!rawMaterialRepository.existsById(id)) {
            throw new RessourceNotFoundException("Material with ID: " + id + " not found.");
        }
        rawMaterialRepository.deleteById(id);
    }

    // --- Retrieval & Utility Methods ---

    @Override
    @Transactional(readOnly = true)
    public RawMaterialResponse findRawMaterialById(Long id) {
        RawMaterial material = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Material with ID: " + id + " not found."));
        return rawMaterialMapper.toResponse(material);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RawMaterialResponse> findAllRawMaterial() {
        return rawMaterialMapper.toResponseList(rawMaterialRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RawMaterialResponse> findByName(String name) {
        return rawMaterialRepository.findByName(name)
                .map(rawMaterialMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return rawMaterialRepository.existsByName(name);
    }

    // --- Business Methods ---

    @Override
    @Transactional(readOnly = true)
    public List<RawMaterialResponse> searchRawMaterials(String query) {
        return rawMaterialMapper.toResponseList(
                rawMaterialRepository.findByNameContainingIgnoreCase(query)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RawMaterialResponse> getMaterialsAtOrBelowStock(Integer stockThreshold) {
        return rawMaterialMapper.toResponseList(
                rawMaterialRepository.findByStockLessThanEqual(stockThreshold)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RawMaterialResponse> getMaterialsBySupplier(Long supplierId) {
        return rawMaterialMapper.toResponseList(
                rawMaterialRepository.findBySuppliers_Id(supplierId)
        );
    }
}