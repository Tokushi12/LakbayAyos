package edu.cit.ballener.lakbayayos.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // para mu gamit na sa backend
    private const val USE_DEPLOYED_BACKEND = true

    // para rani sa emulator
    private const val LOCAL_BASE_URL = "http://10.0.2.2:8080/"

    // iyang actual deployed nga backend url
    private const val DEPLOYED_BASE_URL = "https://lakbayayos-app.onrender.com/"

    private val BASE_URL = if (USE_DEPLOYED_BACKEND) DEPLOYED_BASE_URL else LOCAL_BASE_URL

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }
    val partsApi: PartsApi by lazy { retrofit.create(PartsApi::class.java) }
    val bookingsApi: BookingsApi by lazy { retrofit.create(BookingsApi::class.java) }
}