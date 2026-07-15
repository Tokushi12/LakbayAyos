package edu.cit.ballener.lakbayayos.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.ballener.lakbayayos.data.api.ApiClient
import edu.cit.ballener.lakbayayos.data.model.Part
import edu.cit.ballener.lakbayayos.data.model.PartRequest
import kotlinx.coroutines.launch

class InventoryViewModel : ViewModel() {

    var parts by mutableStateOf<List<Part>>(emptyList())
        private set

    var loading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var submitting by mutableStateOf(false)
        private set

    init {
        loadParts()
    }

    fun loadParts() {
        loading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                parts = ApiClient.partsApi.getAllParts()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load parts."
            } finally {
                loading = false
            }
        }
    }

    fun createPart(
        name: String,
        category: String,
        stockQuantity: Int,
        isAvailable: Boolean,
        imageUrl: String?,
        onResult: (Boolean) -> Unit
    ) {
        submitting = true
        viewModelScope.launch {
            try {
                ApiClient.partsApi.createPart(
                    PartRequest(name, category, stockQuantity, isAvailable, imageUrl?.ifBlank { null })
                )
                loadParts()
                onResult(true)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to save part."
                onResult(false)
            } finally {
                submitting = false
            }
        }
    }

    fun updatePart(
        id: Long,
        name: String,
        category: String,
        stockQuantity: Int,
        isAvailable: Boolean,
        imageUrl: String?,
        onResult: (Boolean) -> Unit
    ) {
        submitting = true
        viewModelScope.launch {
            try {
                ApiClient.partsApi.updatePart(
                    id,
                    PartRequest(name, category, stockQuantity, isAvailable, imageUrl?.ifBlank { null })
                )
                loadParts()
                onResult(true)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to save part."
                onResult(false)
            } finally {
                submitting = false
            }
        }
    }

    fun toggleAvailability(part: Part) {
        viewModelScope.launch {
            try {
                ApiClient.partsApi.updateAvailability(part.id, mapOf("isAvailable" to !part.isAvailable))
                loadParts()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to update availability."
            }
        }
    }

    fun adjustStock(part: Part, newStock: Int) {
        if (newStock < 0) return
        viewModelScope.launch {
            try {
                ApiClient.partsApi.updateStock(part.id, mapOf("stockQuantity" to newStock))
                loadParts()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to update stock."
            }
        }
    }

    fun deletePart(part: Part, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                ApiClient.partsApi.deletePart(part.id)
                loadParts()
                onResult(true)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to delete part."
                onResult(false)
            }
        }
    }
}