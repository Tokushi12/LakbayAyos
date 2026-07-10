package edu.cit.ballener.lakbayayos.Service;
import edu.cit.ballener.lakbayayos.DTO.request.BookingItemRequest;
import edu.cit.ballener.lakbayayos.DTO.request.BookingRequest;
import edu.cit.ballener.lakbayayos.DTO.request.BookingStatusUpdateRequest;
import edu.cit.ballener.lakbayayos.DTO.response.BookingResponse;
import edu.cit.ballener.lakbayayos.Entity.Booking;
import edu.cit.ballener.lakbayayos.Entity.BookingItem;
import edu.cit.ballener.lakbayayos.Entity.Part;
import edu.cit.ballener.lakbayayos.Entity.User;
import edu.cit.ballener.lakbayayos.Exception.*;
import edu.cit.ballener.lakbayayos.Mapper.BookingMapper;
import edu.cit.ballener.lakbayayos.Repository.BookingRepository;
import edu.cit.ballener.lakbayayos.Repository.PartRepository;
import edu.cit.ballener.lakbayayos.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PartRepository partRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          PartRepository partRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.partRepository = partRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("No user found with id: " + request.getUserId()));

        Booking booking = new Booking(user, request.getBookingDate(), "pending");

        List<BookingItem> items = new ArrayList<>();
        for (BookingItemRequest itemRequest : request.getItems()) {
            Part part = partRepository.findById(itemRequest.getPartId())
                    .orElseThrow(() -> new PartNotFoundException("Part not found with id: " + itemRequest.getPartId()));

            if (!Boolean.TRUE.equals(part.getIsAvailable())) {
                throw new PartUnavailableException("Part is not available for booking: " + part.getName());
            }

            BookingItem item = new BookingItem(booking, part, itemRequest.getQuantity());
            items.add(item);
        }

        booking.setBookingItems(items);
        Booking saved = bookingRepository.save(booking);
        return BookingMapper.toResponse(saved);
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(BookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsByUser(String userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(BookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsByStatus(String status) {
        return bookingRepository.findByStatus(status).stream()
                .map(BookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));
        return BookingMapper.toResponse(booking);
    }

    // Used by the admin to approve, decline, or cancel a booking
    @Transactional
    public BookingResponse updateBookingStatus(Long id, BookingStatusUpdateRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));

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
        return BookingMapper.toResponse(updated);
    }

    // Used by the user to cancel their own booking, only allowed while pending
    @Transactional
    public BookingResponse cancelBookingByUser(Long id, String userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));

        if (!booking.getUser().getId().equals(userId)) {
            throw new BookingNotFoundException("Booking not found with id: " + id);
        }

        if (!"pending".equals(booking.getStatus())) {
            throw new InvalidBookingStatusException("Only pending bookings can be cancelled by the user.");
        }

        booking.setStatus("cancelled");
        Booking updated = bookingRepository.save(booking);
        return BookingMapper.toResponse(updated);
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
}