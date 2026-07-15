package edu.cit.ballener.lakbayayos.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object SupabaseAuthClient {

    // Same Supabase project as your React web app.
    private const val SUPABASE_URL = "https://vfiyxgbyuqdiniupcsrd.supabase.co/"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZmaXl4Z2J5dXFkaW5pdXBjc3JkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODI4ODczMDgsImV4cCI6MjA5ODQ2MzMwOH0.BsBfh5NfdpCfOzK6RHyhfZh4NhQBDY7P3xut9KC5CEg"

    // Only "apikey" is added globally. "Authorization" is passed explicitly
    // per-call, since some calls (like updating a password) need the
    // logged-in user's own access token instead of the anon key.
    private val apiKeyInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("apikey", SUPABASE_ANON_KEY)
            .build()
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(SUPABASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: SupabaseAuthApi by lazy { retrofit.create(SupabaseAuthApi::class.java) }
}