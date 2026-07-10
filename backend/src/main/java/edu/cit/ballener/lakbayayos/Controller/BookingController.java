package edu.cit.ballener.lakbayayos.Controller;
import edu.cit.ballener.lakbayayos.DTO.request.BookingRequest;
import edu.cit.ballener.lakbayayos.DTO.request.BookingStatusUpdateRequest;
import edu.cit.ballener.lakbayayos.DTO.response.BookingResponse;
import edu.cit.ballener.lakbayayos.Service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // User: create a new booking with one or more parts
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Admin: view all bookings
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // Admin: filter bookings by status (e.g. only pending ones to review)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookingResponse>> getBookingsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(bookingService.getBookingsByStatus(status));
    }

    // User: view their own bookings
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }

    // Admin: view full details of a specific booking
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    // Admin: approve, decline, or cancel a booking
    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @Valid @RequestBody BookingStatusUpdateRequest request) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, request));
    }

    // User: cancel their own booking, only allowed while pending
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBookingByUser(
            @PathVariable Long id,
            @RequestParam String userId) {
        return ResponseEntity.ok(bookingService.cancelBookingByUser(id, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.getBookingById(id); // ensures it exists, throws 404 if not
        return ResponseEntity.noContent().build();
    }
}