package edu.cit.ballener.lakbayayos.data.model

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String?,
    val role: String,
    val createdAt: String?
)

data class UpdateUserRequest(
    val fullName: String,
    val phoneNumber: String?
)

data class RegisterRequest(
    val id: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String?,
    val role: String
)
