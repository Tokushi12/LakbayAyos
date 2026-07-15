package edu.cit.ballener.lakbayayos.features.createbooking.controller;

import edu.cit.ballener.lakbayayos.features.createbooking.dto.BookingResponse;
import edu.cit.ballener.lakbayayos.features.createbooking.dto.CreateBookingRequest;
import edu.cit.ballener.lakbayayos.features.createbooking.service.CreateBookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class CreateBookingController {

    private final CreateBookingService createBookingService;

    @Autowired
    public CreateBookingController(CreateBookingService createBookingService) {
        this.createBookingService = createBookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        BookingResponse response = createBookingService.handle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(createBookingService.getBookingsByUser(userId));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBookingByUser(
            @PathVariable Long id,
            @RequestParam String userId) {
        return ResponseEntity.ok(createBookingService.cancelBookingByUser(id, userId));
    }
}