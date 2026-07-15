package edu.cit.ballener.lakbayayos.features.searchparts.service;

import edu.cit.ballener.lakbayayos.features.searchparts.dto.PartResult;
import edu.cit.ballener.lakbayayos.shared.entity.Part;
import edu.cit.ballener.lakbayayos.shared.repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchPartsService {

    private final PartRepository partRepository;

    @Autowired
    public SearchPartsService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public List<PartResult> getAvailableParts() {
        return partRepository.findByIsAvailableTrue().stream()
                .map(this::toResult)
                .collect(Collectors.toList());
    }

    public List<PartResult> search(String query) {
        return partRepository
                .findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(query, query).stream()
                .map(this::toResult)
                .collect(Collectors.toList());
    }

    private PartResult toResult(Part part) {
        return new PartResult(
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
