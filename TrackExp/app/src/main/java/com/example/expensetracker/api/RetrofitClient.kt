package com.example.expensetracker.api

import android.content.Context
import com.example.expensetracker.data.local.SharedPreferencesManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // For emulator, 10.0.2.2 points to your computer's localhost
    // For physical device, replace with your computer's local network IP
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    private fun createOkHttpClient(context: Context): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Use Level.NONE for release builds
        }
        val prefsManager = SharedPreferencesManager(context) // Context needed for SharedPreferencesManager

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(prefsManager)) // Add our AuthInterceptor
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // We need context to initialize OkHttpClient which in turn initializes SharedPreferencesManager for AuthInterceptor
    fun getClient(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ApiService instance will now be created with context
    // Example: val apiService = RetrofitClient.getClient(applicationContext).create(ApiService::class.java)
}