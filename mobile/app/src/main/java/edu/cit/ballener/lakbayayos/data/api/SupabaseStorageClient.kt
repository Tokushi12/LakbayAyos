package edu.cit.ballener.lakbayayos.data.api

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

object SupabaseStorageClient {

    private const val SUPABASE_URL = "https://vfiyxgbyuqdiniupcsrd.supabase.co/"
    private const val BUCKET_NAME = "part-images"
    private const val MAX_DIMENSION = 1280

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

    // para universal file upload
    suspend fun uploadPartImage(context: Context, uri: Uri, accessToken: String): String {
        val originalBitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        } ?: throw IllegalStateException("Could not read the selected image.")

        val bitmap = downscaleIfNeeded(originalBitmap)

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val bytes = outputStream.toByteArray()

        val fileName = "${System.currentTimeMillis()}-${(0..999999).random()}.jpg"
        val bucketAndPath = "$BUCKET_NAME/$fileName"
        val requestBody = bytes.toRequestBody("image/jpeg".toMediaType())

        val response = storageApi.uploadFile(
            bucketAndPath = bucketAndPath,
            authorization = "Bearer $accessToken",
            contentType = "image/jpeg",
            file = requestBody
        )

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            val detail = if (!errorBody.isNullOrBlank()) errorBody else response.message()
            throw IllegalStateException("Upload failed (${response.code()}): $detail")
        }

        return "$SUPABASE_URL" + "storage/v1/object/public/$bucketAndPath"
    }

    private fun downscaleIfNeeded(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val largestSide = maxOf(width, height)

        if (largestSide <= MAX_DIMENSION) {
            return bitmap
        }

        val scale = MAX_DIMENSION.toFloat() / largestSide
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}