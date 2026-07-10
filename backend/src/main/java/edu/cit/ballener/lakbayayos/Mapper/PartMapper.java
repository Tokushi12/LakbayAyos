package edu.cit.ballener.lakbayayos.Mapper;
import edu.cit.ballener.lakbayayos.DTO.request.PartRequest;
import edu.cit.ballener.lakbayayos.DTO.response.PartResponse;
import edu.cit.ballener.lakbayayos.Entity.Part;

public class PartMapper {

    public static Part toEntity(PartRequest request) {
        Part part = new Part();
        part.setName(request.getName());
        part.setCategory(request.getCategory());
        part.setStockQuantity(request.getStockQuantity());
        part.setIsAvailable(request.getIsAvailable());
        return part;
    }

    public static PartResponse toResponse(Part part) {
        return new PartResponse(
                part.getId(),
                part.getName(),
                part.getCategory(),
                part.getStockQuantity(),
                part.getIsAvailable(),
                part.getCreatedAt(),
                part.getUpdatedAt()
        );
    }
}