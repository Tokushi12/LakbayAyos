package edu.cit.ballener.lakbayayos.features.manageinventory.service;

import edu.cit.ballener.lakbayayos.exception.PartNotFoundException;
import edu.cit.ballener.lakbayayos.features.manageinventory.dto.PartRequest;
import edu.cit.ballener.lakbayayos.features.manageinventory.dto.PartResponse;
import edu.cit.ballener.lakbayayos.shared.entity.Part;
import edu.cit.ballener.lakbayayos.shared.repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManageInventoryService {

    private final PartRepository partRepository;

    @Autowired
    public ManageInventoryService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public PartResponse createPart(PartRequest request) {
        Part part = new Part();
        part.setName(request.getName());
        part.setCategory(request.getCategory());
        part.setStockQuantity(request.getStockQuantity());
        part.setIsAvailable(request.getIsAvailable());
        part.setImageUrl(request.getImageUrl());

        Part saved = partRepository.save(part);
        return toResponse(saved);
    }

    public List<PartResponse> getAllParts() {
        return partRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PartResponse getPartById(Long id) {
        Part part = findOrThrow(id);
        return toResponse(part);
    }

    public PartResponse updatePart(Long id, PartRequest request) {
        Part part = findOrThrow(id);

        part.setName(request.getName());
        part.setCategory(request.getCategory());
        part.setStockQuantity(request.getStockQuantity());
        part.setIsAvailable(request.getIsAvailable());
        part.setImageUrl(request.getImageUrl());

        Part updated = partRepository.save(part);
        return toResponse(updated);
    }

    public PartResponse updateAvailability(Long id, Boolean isAvailable) {
        Part part = findOrThrow(id);
        part.setIsAvailable(isAvailable);
        Part updated = partRepository.save(part);
        return toResponse(updated);
    }

    public PartResponse updateStock(Long id, Integer stockQuantity) {
        Part part = findOrThrow(id);
        part.setStockQuantity(stockQuantity);
        Part updated = partRepository.save(part);
        return toResponse(updated);
    }

    public void deletePart(Long id) {
        Part part = findOrThrow(id);
        partRepository.delete(part);
    }

    private Part findOrThrow(Long id) {
        return partRepository.findById(id)
                .orElseThrow(() -> new PartNotFoundException("Part not found with id: " + id));
    }

    private PartResponse toResponse(Part part) {
        return new PartResponse(
                part.getId(),
                part.getName(),
                part.getCategory(),
                part.getStockQuantity(),
                part.getIsAvailable(),
                part.getImageUrl(),
                part.getCreatedAt(),
                part.getUpdatedAt()
        );
    }
}
