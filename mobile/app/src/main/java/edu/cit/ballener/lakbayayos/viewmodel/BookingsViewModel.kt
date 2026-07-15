package edu.cit.ballener.lakbayayos.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.ballener.lakbayayos.data.api.ApiClient
import edu.cit.ballener.lakbayayos.data.model.Booking
import edu.cit.ballener.lakbayayos.data.model.BookingStatusUpdateRequest
import kotlinx.coroutines.launch

class BookingsViewModel : ViewModel() {

    var bookings by mutableStateOf<List<Booking>>(emptyList())
        private set

    var loading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var actionInProgress by mutableStateOf(false)
        private set

    // Customer: load only their own bookings
    fun loadForUser(userId: String) {
        loading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                bookings = ApiClient.bookingsApi.getBookingsByUser(userId)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load bookings."
            } finally {
                loading = false
            }
        }
    }

    // Admin: load every booking
    fun loadAll() {
        loading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                bookings = ApiClient.bookingsApi.getAllBookings()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load bookings."
            } finally {
                loading = false
            }
        }
    }

    // Customer: cancel their own pending booking
    fun cancelBooking(bookingId: Long, userId: String, onResult: (Boolean) -> Unit) {
        actionInProgress = true

        viewModelScope.launch {
            try {
                ApiClient.bookingsApi.cancelBookingByUser(bookingId, userId)
                loadForUser(userId)
                onResult(true)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to cancel booking."
                onResult(false)
            } finally {
                actionInProgress = false
            }
        }
    }

    // Admin: approve, decline, or cancel any booking
    fun updateStatus(bookingId: Long, status: String, adminNotes: String?, onResult: (Boolean) -> Unit) {
        actionInProgress = true

        viewModelScope.launch {
            try {
                ApiClient.bookingsApi.updateBookingStatus(
                    bookingId,
                    BookingStatusUpdateRequest(status = status, adminNotes = adminNotes)
                )
                loadAll()
                onResult(true)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to update booking."
                onResult(false)
            } finally {
                actionInProgress = false
            }
        }
    }
}