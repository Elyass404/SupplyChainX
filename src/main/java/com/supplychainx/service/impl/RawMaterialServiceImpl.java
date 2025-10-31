package com.supplychainx.service.impl;

import com.supplychainx.exception.NameAlreadyExistsException;
import com.supplychainx.exception.RessourceNotFoundException;
import com.supplychainx.model.RawMaterial;
import com.supplychainx.repository.RawMaterialRepository;
import com.supplychainx.service.RawMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RawMaterialServiceImpl  implements RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;

    @Override
    public RawMaterial createRawMaterial(RawMaterial rawMaterial) {
        if(rawMaterialRepository.existsByName(rawMaterial.getName())){
            throw new NameAlreadyExistsException("Material with the Name: "  + rawMaterial.getName() + " already exists");
        }
        return rawMaterialRepository.save(rawMaterial);
    }

    @Override
    public RawMaterial updateRawMaterial(Long id, RawMaterial rawMaterial) {
        RawMaterial existingRawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(()-> new RessourceNotFoundException("Material with ID: " + id + " not found"));

        existingRawMaterial.setName(rawMaterial.getName());
        existingRawMaterial.setStock(rawMaterial.getStock());
        existingRawMaterial.setStockMin(rawMaterial.getStockMin());
        existingRawMaterial.setStock(rawMaterial.getStock());
        existingRawMaterial.setUnit(rawMaterial.getUnit());

        rawMaterialRepository.save(existingRawMaterial);
    }

    @Override
    public void deleteRawMaterial(RawMaterial rawMaterial) {

    }

    @Override
    public Optional<RawMaterial> findRawMaterialById(Long id) {
        return null;
    }

    @Override
    public List<RawMaterial> findAllRawMaterial() {
        return null;
    }

    @Override
    public RawMaterial findByName(String name) {
        return null;
    }

    @Override
    public boolean existsByName(String name){
        return false;
    }

    @Override
    public List<RawMaterial>findByNameContainingIgnoreCase(String fragment){
        return null;
    }

    @Override
    public List<RawMaterial> findByStockLessThanEqual(Integer stockThreshold) {
        return null;
    }

    @Override
    public List<RawMaterial> findBySuppliers_Id(Long supplierId) {
        return null;
    }

}
