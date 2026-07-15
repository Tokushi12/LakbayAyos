package edu.cit.ballener.lakbayayos.data.api

import edu.cit.ballener.lakbayayos.data.model.Booking
import edu.cit.ballener.lakbayayos.data.model.BookingRequest
import edu.cit.ballener.lakbayayos.data.model.BookingStatusUpdateRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BookingsApi {

    // Used by customers
    @POST("/api/bookings")
    suspend fun createBooking(@Body request: BookingRequest): Booking

    @GET("/api/bookings/user/{userId}")
    suspend fun getBookingsByUser(@Path("userId") userId: String): List<Booking>

    @PATCH("/api/bookings/{id}/cancel")
    suspend fun cancelBookingByUser(@Path("id") id: Long, @Query("userId") userId: String): Booking

    // Used by admin
    @GET("/api/bookings")
    suspend fun getAllBookings(): List<Booking>

    @GET("/api/bookings/status/{status}")
    suspend fun getBookingsByStatus(@Path("status") status: String): List<Booking>

    @PATCH("/api/bookings/{id}/status")
    suspend fun updateBookingStatus(@Path("id") id: Long, @Body request: BookingStatusUpdateRequest): Booking
}
