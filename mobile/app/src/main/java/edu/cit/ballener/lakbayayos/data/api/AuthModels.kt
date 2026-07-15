package edu.cit.ballener.lakbayayos.data.api

data class SignUpRequest(
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthUser(
    val id: String,
    val email: String?
)

data class AuthResponse(
    val access_token: String?,
    val user: AuthUser
)
