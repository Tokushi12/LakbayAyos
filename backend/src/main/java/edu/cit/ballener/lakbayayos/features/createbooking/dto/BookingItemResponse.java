package edu.cit.ballener.lakbayayos.features.createbooking.dto;

public class BookingItemResponse {

    private Long id;
    private Long partId;
    private String partName;
    private String partCategory;
    private Integer quantity;

    public BookingItemResponse() {
    }

    public BookingItemResponse(Long id, Long partId, String partName, String partCategory, Integer quantity) {
        this.id = id;
        this.partId = partId;
        this.partName = partName;
        this.partCategory = partCategory;
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPartId() { return partId; }
    public void setPartId(Long partId) { this.partId = partId; }

    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public String getPartCategory() { return partCategory; }
    public void setPartCategory(String partCategory) { this.partCategory = partCategory; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
