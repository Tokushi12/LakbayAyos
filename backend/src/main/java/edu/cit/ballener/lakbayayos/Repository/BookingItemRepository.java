package edu.cit.ballener.lakbayayos.Repository;
import edu.cit.ballener.lakbayayos.Entity.BookingItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingItemRepository extends JpaRepository<BookingItem, Long> {

    List<BookingItem> findByBookingId(Long bookingId);
}