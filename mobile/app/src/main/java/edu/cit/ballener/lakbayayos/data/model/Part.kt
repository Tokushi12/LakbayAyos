package edu.cit.ballener.lakbayayos.data.model

data class Part(
    val id: Long,
    val name: String,
    val category: String,
    val stockQuantity: Int,
    val isAvailable: Boolean,
    val imageUrl: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class PartRequest(
    val name: String,
    val category: String,
    val stockQuantity: Int,
    val isAvailable: Boolean,
    val imageUrl: String?
)
