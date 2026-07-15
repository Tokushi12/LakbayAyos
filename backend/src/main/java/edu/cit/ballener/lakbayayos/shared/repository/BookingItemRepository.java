package edu.cit.ballener.lakbayayos.shared.repository;
import edu.cit.ballener.lakbayayos.shared.entity.BookingItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingItemRepository extends JpaRepository<BookingItem, Long> {

    List<BookingItem> findByBookingId(Long bookingId);
}