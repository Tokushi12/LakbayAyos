package edu.cit.ballener.lakbayayos.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.ballener.lakbayayos.data.api.ApiClient
import edu.cit.ballener.lakbayayos.data.model.BookingItemRequest
import edu.cit.ballener.lakbayayos.data.model.BookingRequest
import edu.cit.ballener.lakbayayos.data.model.Part
import kotlinx.coroutines.launch

data class CartItem(
    val partId: Long,
    val name: String,
    val category: String,
    var quantity: Int
)

class PartsViewModel : ViewModel() {

    var parts by mutableStateOf<List<Part>>(emptyList())
        private set

    var loading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    var submitting by mutableStateOf(false)
        private set

    val cart = mutableStateListOf<CartItem>()

    init {
        loadParts()
    }

    fun loadParts() {
        loading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                parts = ApiClient.partsApi.getAvailableParts()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load parts."
            } finally {
                loading = false
            }
        }
    }

    fun search(query: String) {
        loading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                parts = if (query.isBlank()) {
                    ApiClient.partsApi.getAvailableParts()
                } else {
                    ApiClient.partsApi.searchParts(query).filter { it.isAvailable }
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Search failed."
            } finally {
                loading = false
            }
        }
    }

    fun addToCart(part: Part) {
        val existing = cart.find { it.partId == part.id }
        if (existing != null) {
            existing.quantity += 1
            val index = cart.indexOf(existing)
            cart[index] = existing.copy(quantity = existing.quantity)
        } else {
            cart.add(CartItem(partId = part.id, name = part.name, category = part.category, quantity = 1))
        }
    }

    fun updateQuantity(partId: Long, quantity: Int) {
        if (quantity < 1) return
        val index = cart.indexOfFirst { it.partId == partId }
        if (index != -1) cart[index] = cart[index].copy(quantity = quantity)
    }

    fun removeFromCart(partId: Long) {
        cart.removeAll { it.partId == partId }
    }

    fun submitBooking(userId: String, bookingDate: String, onResult: (Boolean) -> Unit) {
        if (cart.isEmpty()) return
        submitting = true
        errorMessage = null
        successMessage = null

        viewModelScope.launch {
            try {
                ApiClient.bookingsApi.createBooking(
                    BookingRequest(
                        userId = userId,
                        bookingDate = bookingDate,
                        items = cart.map { BookingItemRequest(partId = it.partId, quantity = it.quantity) }
                    )
                )
                cart.clear()
                successMessage = "Booking request submitted. You can track its status in My Bookings."
                onResult(true)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to submit booking."
                onResult(false)
            } finally {
                submitting = false
            }
        }
    }
}