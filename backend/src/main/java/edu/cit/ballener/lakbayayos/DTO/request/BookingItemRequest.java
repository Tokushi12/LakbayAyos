package edu.cit.ballener.lakbayayos.DTO.request;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BookingItemRequest {

    @NotNull(message = "Part id is required")
    private Long partId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    public BookingItemRequest() {
    }

    // Getters and setters
    public Long getPartId() { return partId; }
    public void setPartId(Long partId) { this.partId = partId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}