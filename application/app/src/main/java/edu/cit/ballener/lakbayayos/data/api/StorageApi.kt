package edu.cit.ballener.lakbayayos.data.api

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface StorageApi {

    // PUT with upsert semantics - path includes bucket name and generated
    // filename, e.g. "part-images/172930123-abc.jpg"
    @PUT("storage/v1/object/{bucketAndPath}")
    suspend fun uploadFile(
        @Path(value = "bucketAndPath", encoded = true) bucketAndPath: String,
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String,
        @Header("x-upsert") upsert: String = "true",
        @retrofit2.http.Body file: RequestBody
    ): Response<Unit>
}
