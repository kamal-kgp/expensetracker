package com.example.expensetracker.api

import com.example.expensetracker.data.local.SharedPreferencesManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sharedPreferencesManager: SharedPreferencesManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        val token = sharedPreferencesManager.getAuthToken()

        // Add token only if it exists and the request is not for auth endpoints
        if (!token.isNullOrEmpty()) {
            val originalRequestPath = chain.request().url.encodedPath
            if (!originalRequestPath.contains("/api/auth/signup") &&
                !originalRequestPath.contains("/api/auth/signin")) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
        }
        return chain.proceed(requestBuilder.build())
    }
}