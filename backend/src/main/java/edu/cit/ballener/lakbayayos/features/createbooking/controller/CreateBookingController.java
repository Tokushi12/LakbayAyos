package edu.cit.ballener.lakbayayos.features.createbooking.controller;

import edu.cit.ballener.lakbayayos.features.createbooking.dto.BookingResponse;
import edu.cit.ballener.lakbayayos.features.createbooking.dto.CreateBookingRequest;
import edu.cit.ballener.lakbayayos.features.createbooking.service.CreateBookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
