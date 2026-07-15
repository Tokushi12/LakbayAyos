package edu.cit.ballener.lakbayayos.shared.repository;
import edu.cit.ballener.lakbayayos.shared.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(String userId);

    List<Booking> findByStatus(String status);

    List<Booking> findByUserIdAndStatus(String userId, String status);
}