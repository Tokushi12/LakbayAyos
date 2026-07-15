package edu.cit.ballener.lakbayayos.data.api

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object SupabaseStorageClient {

    private const val SUPABASE_URL = "https://vfiyxgbyuqdiniupcsrd.supabase.co/"
    private const val BUCKET_NAME = "part-images"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", SupabaseAuthClient.SUPABASE_ANON_KEY)
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(SUPABASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val storageApi: StorageApi by lazy { retrofit.create(StorageApi::class.java) }

    // Reads the picked image's bytes, uploads it to the part-images bucket
    // under a unique generated filename, and returns the public URL - the
    // same kind of URL the web app's storageApi.js produces.
    suspend fun uploadPartImage(context: Context, uri: Uri, accessToken: String): String {
        val contentResolver: ContentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
        val extension = when {
            mimeType.contains("png") -> "png"
            mimeType.contains("webp") -> "webp"
            else -> "jpg"
        }

        val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw IllegalStateException("Could not read the selected image.")

        val fileName = "${System.currentTimeMillis()}-${(0..999999).random()}.$extension"
        val bucketAndPath = "$BUCKET_NAME/$fileName"
        val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())

        val response = storageApi.uploadFile(
            bucketAndPath = bucketAndPath,
            authorization = "Bearer $accessToken",
            contentType = mimeType,
            file = requestBody
        )

        if (!response.isSuccessful) {
            throw IllegalStateException("Upload failed: ${response.code()} ${response.message()}")
        }

        return "$SUPABASE_URL" + "storage/v1/object/public/$bucketAndPath"
    }
}
