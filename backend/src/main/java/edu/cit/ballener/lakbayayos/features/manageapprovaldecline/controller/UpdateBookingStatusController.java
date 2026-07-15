package edu.cit.ballener.lakbayayos.features.manageapprovaldecline.controller;

import edu.cit.ballener.lakbayayos.features.manageapprovaldecline.dto.BookingResponse;
import edu.cit.ballener.lakbayayos.features.manageapprovaldecline.dto.BookingStatusUpdateRequest;
import edu.cit.ballener.lakbayayos.features.manageapprovaldecline.service.UpdateBookingStatusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class UpdateBookingStatusController {

    private final UpdateBookingStatusService updateBookingStatusService;

    @Autowired
    public UpdateBookingStatusController(UpdateBookingStatusService updateBookingStatusService) {
        this.updateBookingStatusService = updateBookingStatusService;
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(updateBookingStatusService.getAllBookings());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookingResponse>> getBookingsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(updateBookingStatusService.getBookingsByStatus(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(updateBookingStatusService.getBookingById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody BookingStatusUpdateRequest request) {
        return ResponseEntity.ok(updateBookingStatusService.handle(id, request));
    }
}
