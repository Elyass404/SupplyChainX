package com.supplychainx.service.impl;

import com.supplychainx.exception.NameAlreadyExistsException;
import com.supplychainx.exception.RessourceNotFoundException;
import com.supplychainx.model.RawMaterial;
import com.supplychainx.repository.RawMaterialRepository;
import com.supplychainx.service.RawMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Good practice for service methods

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RawMaterialServiceImpl implements RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;

    // --- CRUD ---

    @Override
    public RawMaterial createRawMaterial(RawMaterial rawMaterial) {
        //lets exist existence check before save (prevents DB unique constraint violation)
        if (rawMaterialRepository.existsByName(rawMaterial.getName())) {
            throw new NameAlreadyExistsException("Material with the Name: " + rawMaterial.getName() + " already exists.");
        }
        return rawMaterialRepository.save(rawMaterial);
    }

    @Override
    public RawMaterial updateRawMaterial(Long id, RawMaterial rawMaterial) {
        // first we check the existence of the material
        RawMaterial existingRawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Material with ID: " + id + " not found."));

        // Check for name change conflict (Only check if the name is changing)
        if (!existingRawMaterial.getName().equalsIgnoreCase(rawMaterial.getName()) &&
                rawMaterialRepository.existsByName(rawMaterial.getName())) {
            throw new NameAlreadyExistsException("Cannot update. Material with name '" + rawMaterial.getName() + "' already exists.");
        }

        // Update fields from the DTO/input object
        existingRawMaterial.setName(rawMaterial.getName());
        existingRawMaterial.setStock(rawMaterial.getStock());
        existingRawMaterial.setStockMin(rawMaterial.getStockMin());
        existingRawMaterial.setUnit(rawMaterial.getUnit());


        return rawMaterialRepository.save(existingRawMaterial);
    }

    @Override
    public void deleteRawMaterial(Long id) {
        // first  we check the material if it is existing
        if (!rawMaterialRepository.existsById(id)) {
            throw new RessourceNotFoundException("Material with ID: " + id + " not found.");
        }
        rawMaterialRepository.deleteById(id);
    }

    // --- Retrieval Methods ---

    @Override
    public Optional<RawMaterial> findRawMaterialById(Long id) {

        return rawMaterialRepository.findById(id);
    }

    @Override
    public List<RawMaterial> findAllRawMaterial() {
        return rawMaterialRepository.findAll();
    }

    @Override
    public Optional<RawMaterial> findByName(String name) {

        return rawMaterialRepository.findByName(name);
    }

    // --- Utility & Business Methods (New Names) ---

    @Override
    public boolean existsByName(String name) {

        return rawMaterialRepository.existsByName(name);
    }

    @Override
    public List<RawMaterial> searchRawMaterials(String name) {

        return rawMaterialRepository.findByNameContainingIgnoreCase(name);
    }


    @Override
    public List<RawMaterial> getMaterialsAtOrBelowStock(Integer stockThreshold) {

        return rawMaterialRepository.findByStockLessThanEqual(stockThreshold);
    }

    @Override
    public List<RawMaterial> getMaterialsBySupplier(Long supplierId) {

        return rawMaterialRepository.findBySuppliers_Id(supplierId);
    }
}