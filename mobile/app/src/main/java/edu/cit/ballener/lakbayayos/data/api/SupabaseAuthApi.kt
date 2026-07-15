package edu.cit.ballener.lakbayayos.data.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

data class UpdatePasswordRequest(
    val password: String
)

interface SupabaseAuthApi {

    @Headers("Content-Type: application/json")
    @POST("auth/v1/signup")
    suspend fun signUp(@Body request: SignUpRequest): AuthResponse

    @Headers("Content-Type: application/json")
    @POST("auth/v1/token")
    suspend fun login(
        @Query("grant_type") grantType: String = "password",
        @Body request: LoginRequest
    ): AuthResponse

    // Requires the logged-in user's own access token (not the anon key)
    // so Supabase knows which account's password to change.
    @Headers("Content-Type: application/json")
    @PUT("auth/v1/user")
    suspend fun updatePassword(
        @Header("Authorization") authorization: String,
        @Body request: UpdatePasswordRequest
    ): AuthUser
}