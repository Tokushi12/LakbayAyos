package edu.cit.ballener.lakbayayos.features.manageapprovaldecline.service;

import edu.cit.ballener.lakbayayos.exception.BookingNotFoundException;
import edu.cit.ballener.lakbayayos.exception.InsufficientStockException;
import edu.cit.ballener.lakbayayos.exception.InvalidBookingStatusException;
import edu.cit.ballener.lakbayayos.features.manageapprovaldecline.dto.BookingItemResponse;
import edu.cit.ballener.lakbayayos.features.manageapprovaldecline.dto.BookingResponse;
import edu.cit.ballener.lakbayayos.features.manageapprovaldecline.dto.BookingStatusUpdateRequest;
import edu.cit.ballener.lakbayayos.shared.entity.Booking;
import edu.cit.ballener.lakbayayos.shared.entity.BookingItem;
import edu.cit.ballener.lakbayayos.shared.entity.Part;
import edu.cit.ballener.lakbayayos.shared.repository.BookingRepository;
import edu.cit.ballener.lakbayayos.shared.repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UpdateBookingStatusService {

    private final BookingRepository bookingRepository;
    private final PartRepository partRepository;

    @Autowired
    public UpdateBookingStatusService(BookingRepository bookingRepository, PartRepository partRepository) {
        this.bookingRepository = bookingRepository;
        this.partRepository = partRepository;
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsByStatus(String status) {
        return bookingRepository.findByStatus(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BookingResponse getBookingById(Long id) {
        Booking booking = findOrThrow(id);
        return toResponse(booking);
    }

    @Transactional
    public BookingResponse handle(Long id, BookingStatusUpdateRequest request) {
        Booking booking = findOrThrow(id);

        String currentStatus = booking.getStatus();
        String newStatus = request.getStatus();

        validateStatusTransition(currentStatus, newStatus);

        if ("approved".equals(newStatus) && !"approved".equals(currentStatus)) {
            deductStock(booking);
        }

        if (("declined".equals(newStatus) || "cancelled".equals(newStatus)) && "approved".equals(currentStatus)) {
            restoreStock(booking);
        }

        booking.setStatus(newStatus);
        booking.setAdminNotes(request.getAdminNotes());

        Booking updated = bookingRepository.save(booking);
        return toResponse(updated);
    }

    private void deductStock(Booking booking) {
        for (BookingItem item : booking.getBookingItems()) {
            Part part = item.getPart();
            int newStock = part.getStockQuantity() - item.getQuantity();

            if (newStock < 0) {
                throw new InsufficientStockException("Not enough stock for part: " + part.getName());
            }

            part.setStockQuantity(newStock);
            partRepository.save(part);
        }
    }

    private void restoreStock(Booking booking) {
        for (BookingItem item : booking.getBookingItems()) {
            Part part = item.getPart();
            part.setStockQuantity(part.getStockQuantity() + item.getQuantity());
            partRepository.save(part);
        }
    }

    private void validateStatusTransition(String currentStatus, String newStatus) {
        List<String> validStatuses = List.of("pending", "approved", "declined", "cancelled");

        if (!validStatuses.contains(newStatus)) {
            throw new InvalidBookingStatusException("Invalid status: " + newStatus);
        }

        if ("cancelled".equals(currentStatus) || "declined".equals(currentStatus)) {
            throw new InvalidBookingStatusException(
                    "Cannot change status of a booking that is already " + currentStatus);
        }
    }

    private Booking findOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));
    }

    private BookingResponse toResponse(Booking booking) {
        List<BookingItemResponse> itemResponses = booking.getBookingItems().stream()
                .map(item -> new BookingItemResponse(
                        item.getId(),
                        item.getPart().getId(),
                        item.getPart().getName(),
                        item.getPart().getCategory(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());

        return new BookingResponse(
                booking.getId(),
                booking.getUser().getId(),
                booking.getUser().getFullName(),
                booking.getBookingDate(),
                booking.getStatus(),
                booking.getAdminNotes(),
                itemResponses,
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );
    }
}
