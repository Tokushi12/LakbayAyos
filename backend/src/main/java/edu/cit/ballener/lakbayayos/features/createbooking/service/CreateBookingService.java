package edu.cit.ballener.lakbayayos.features.createbooking.service;

import edu.cit.ballener.lakbayayos.exception.PartNotFoundException;
import edu.cit.ballener.lakbayayos.exception.PartUnavailableException;
import edu.cit.ballener.lakbayayos.exception.UserNotFoundException;
import edu.cit.ballener.lakbayayos.features.createbooking.dto.BookingItemRequest;
import edu.cit.ballener.lakbayayos.features.createbooking.dto.BookingItemResponse;
import edu.cit.ballener.lakbayayos.features.createbooking.dto.BookingResponse;
import edu.cit.ballener.lakbayayos.features.createbooking.dto.CreateBookingRequest;
import edu.cit.ballener.lakbayayos.shared.entity.Booking;
import edu.cit.ballener.lakbayayos.shared.entity.BookingItem;
import edu.cit.ballener.lakbayayos.shared.entity.Part;
import edu.cit.ballener.lakbayayos.shared.entity.User;
import edu.cit.ballener.lakbayayos.shared.repository.BookingRepository;
import edu.cit.ballener.lakbayayos.shared.repository.PartRepository;
import edu.cit.ballener.lakbayayos.shared.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreateBookingService {

    private final BookingRepository bookingRepository;
    private final PartRepository partRepository;
    private final UserRepository userRepository;

    @Autowired
    public CreateBookingService(BookingRepository bookingRepository,
                                 PartRepository partRepository,
                                 UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.partRepository = partRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingResponse handle(CreateBookingRequest request) {
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

            items.add(new BookingItem(booking, part, itemRequest.getQuantity()));
        }

        booking.setBookingItems(items);
        Booking saved = bookingRepository.save(booking);

        return toResponse(saved);
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
