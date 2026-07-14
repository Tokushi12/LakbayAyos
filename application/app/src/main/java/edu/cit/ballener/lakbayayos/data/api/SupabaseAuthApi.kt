package edu.cit.ballener.lakbayayos.data.api

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

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
}
