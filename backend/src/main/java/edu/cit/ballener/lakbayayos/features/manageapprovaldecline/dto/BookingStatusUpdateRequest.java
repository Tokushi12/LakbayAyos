package edu.cit.ballener.lakbayayos.features.manageapprovaldecline.dto;

import jakarta.validation.constraints.NotBlank;

public class BookingStatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status; // approved, declined, cancelled

    private String adminNotes;

    public BookingStatusUpdateRequest() {
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
}
