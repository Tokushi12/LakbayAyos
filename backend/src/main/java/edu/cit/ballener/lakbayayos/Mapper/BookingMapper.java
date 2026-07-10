package edu.cit.ballener.lakbayayos.Mapper;
import edu.cit.ballener.lakbayayos.DTO.response.BookingItemResponse;
import edu.cit.ballener.lakbayayos.DTO.response.BookingResponse;
import edu.cit.ballener.lakbayayos.Entity.Booking;
import edu.cit.ballener.lakbayayos.Entity.BookingItem;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingItemResponse toItemResponse(BookingItem item) {
        return new BookingItemResponse(
                item.getId(),
                item.getPart().getId(),
                item.getPart().getName(),
                item.getPart().getCategory(),
                item.getQuantity()
        );
    }

    public static BookingResponse toResponse(Booking booking) {
        List<BookingItemResponse> itemResponses = booking.getBookingItems().stream()
                .map(BookingMapper::toItemResponse)
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
