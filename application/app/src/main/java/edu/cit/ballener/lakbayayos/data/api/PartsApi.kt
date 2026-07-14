package edu.cit.ballener.lakbayayos.data.api

import edu.cit.ballener.lakbayayos.data.model.Part
import edu.cit.ballener.lakbayayos.data.model.PartRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PartsApi {

    // Used by customers browsing/searching
    @GET("/api/parts/available")
    suspend fun getAvailableParts(): List<Part>

    @GET("/api/parts/search")
    suspend fun searchParts(@Query("query") query: String): List<Part>

    // Used by admin managing inventory
    @GET("/api/parts")
    suspend fun getAllParts(): List<Part>

    @POST("/api/parts")
    suspend fun createPart(@Body request: PartRequest): Part

    @PUT("/api/parts/{id}")
    suspend fun updatePart(@Path("id") id: Long, @Body request: PartRequest): Part

    @PATCH("/api/parts/{id}/availability")
    suspend fun updateAvailability(@Path("id") id: Long, @Body body: Map<String, Boolean>): Part

    @PATCH("/api/parts/{id}/stock")
    suspend fun updateStock(@Path("id") id: Long, @Body body: Map<String, Int>): Part
}
