package edu.cit.ballener.lakbayayos.shared.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "booking_items")
public class BookingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    private Integer quantity;

    public BookingItem() {
    }

    public BookingItem(Booking booking, Part part, Integer quantity) {
        this.booking = booking;
        this.part = part;
        this.quantity = quantity;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Part getPart() { return part; }
    public void setPart(Part part) { this.part = part; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
