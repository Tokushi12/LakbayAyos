package edu.cit.ballener.lakbayayos.Service;
import edu.cit.ballener.lakbayayos.DTO.request.PartRequest;
import edu.cit.ballener.lakbayayos.DTO.response.PartResponse;
import edu.cit.ballener.lakbayayos.Entity.Part;
import edu.cit.ballener.lakbayayos.Exception.PartNotFoundException;
import edu.cit.ballener.lakbayayos.Mapper.PartMapper;
import edu.cit.ballener.lakbayayos.Repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartService {

    private final PartRepository partRepository;

    @Autowired
    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public PartResponse createPart(PartRequest request) {
        Part part = PartMapper.toEntity(request);
        Part saved = partRepository.save(part);
        return PartMapper.toResponse(saved);
    }

    public List<PartResponse> getAllParts() {
        return partRepository.findAll().stream()
                .map(PartMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<PartResponse> getAvailableParts() {
        return partRepository.findByIsAvailableTrue().stream()
                .map(PartMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<PartResponse> searchParts(String query) {
        return partRepository
                .findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(query, query).stream()
                .map(PartMapper::toResponse)
                .collect(Collectors.toList());
    }

    public PartResponse getPartById(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new PartNotFoundException("Part not found with id: " + id));
        return PartMapper.toResponse(part);
    }

    public PartResponse updatePart(Long id, PartRequest request) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new PartNotFoundException("Part not found with id: " + id));

        part.setName(request.getName());
        part.setCategory(request.getCategory());
        part.setStockQuantity(request.getStockQuantity());
        part.setIsAvailable(request.getIsAvailable());

        Part updated = partRepository.save(part);
        return PartMapper.toResponse(updated);
    }

    public PartResponse updateAvailability(Long id, Boolean isAvailable) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new PartNotFoundException("Part not found with id: " + id));

        part.setIsAvailable(isAvailable);
        Part updated = partRepository.save(part);
        return PartMapper.toResponse(updated);
    }

    public PartResponse updateStock(Long id, Integer stockQuantity) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new PartNotFoundException("Part not found with id: " + id));

        part.setStockQuantity(stockQuantity);
        Part updated = partRepository.save(part);
        return PartMapper.toResponse(updated);
    }

    public void deletePart(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new PartNotFoundException("Part not found with id: " + id));
        partRepository.delete(part);
    }
}