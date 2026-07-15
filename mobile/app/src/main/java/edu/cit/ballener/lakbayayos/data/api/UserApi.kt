package edu.cit.ballener.lakbayayos.data.api

import edu.cit.ballener.lakbayayos.data.model.RegisterRequest
import edu.cit.ballener.lakbayayos.data.model.UpdateUserRequest
import edu.cit.ballener.lakbayayos.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.POST

interface UserApi {

    @POST("/api/users/register")
    suspend fun registerUser(@Body request: RegisterRequest): User

    @GET("/api/users/{id}")
    suspend fun getUserById(@Path("id") id: String): User

    @PUT("/api/users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body request: UpdateUserRequest): User
}
