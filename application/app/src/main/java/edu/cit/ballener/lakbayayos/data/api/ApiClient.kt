package edu.cit.ballener.lakbayayos.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // 10.0.2.2 is the emulator's special alias for your computer's localhost.
    // If you switch to a physical phone on the same Wi-Fi later, replace this
    // with your computer's actual local network IP, e.g. "http://192.168.1.5:8080/"
    private const val BASE_URL = "http://10.0.2.2:8081/"

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
