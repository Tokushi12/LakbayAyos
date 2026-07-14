package edu.cit.ballener.lakbayayos.data.model

data class BookingItem(
    val id: Long,
    val partId: Long,
    val partName: String,
    val partCategory: String,
    val quantity: Int
)

data class Booking(
    val id: Long,
    val userId: String,
    val userFullName: String,
    val bookingDate: String,
    val status: String,
    val adminNotes: String?,
    val items: List<BookingItem>,
    val createdAt: String?,
    val updatedAt: String?
)

data class BookingItemRequest(
    val partId: Long,
    val quantity: Int
)

data class BookingRequest(
    val userId: String,
    val bookingDate: String,
    val items: List<BookingItemRequest>
)

data class BookingStatusUpdateRequest(
    val status: String,
    val adminNotes: String?
)
