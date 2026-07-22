package edu.cit.ballener.lakbayayos.data.api

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface StorageApi {

    @POST("storage/v1/object/{bucketAndPath}")
    suspend fun uploadFile(
        @Path(value = "bucketAndPath", encoded = true) bucketAndPath: String,
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String,
        @retrofit2.http.Body file: RequestBody
    ): Response<Unit>
}