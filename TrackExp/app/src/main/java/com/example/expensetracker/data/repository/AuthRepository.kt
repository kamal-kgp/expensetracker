package com.example.expensetracker.data.repository

import android.content.Context
import com.example.expensetracker.api.ApiService
import com.example.expensetracker.api.RetrofitClient
import com.example.expensetracker.api.models.request.LoginRequest
import com.example.expensetracker.api.models.request.RegisterRequest
import com.example.expensetracker.api.models.response.LoginResponse
import com.example.expensetracker.api.models.response.MessageResponse
import com.example.expensetracker.data.local.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response // Crucial import

// A more robust implementation would use a Result wrapper (e.g., sealed class)
// For simplicity, we'll directly return Retrofit's Response for now.

class AuthRepository(applicationContext: Context) {

    private val apiService: ApiService =
        RetrofitClient.getClient(applicationContext).create(ApiService::class.java)
    private val sharedPreferencesManager: SharedPreferencesManager = SharedPreferencesManager(applicationContext)

    suspend fun login(loginRequest: LoginRequest): Response<LoginResponse> {
        return withContext(Dispatchers.IO) {
            val response = apiService.signin(loginRequest)
            if (response.isSuccessful) {
                response.body()?.let {
                    sharedPreferencesManager.saveAuthToken(it.token)
                    sharedPreferencesManager.saveUserDetails(it.id, it.username, it.email)
                }
            }
            response
        }
    }

    suspend fun register(registerRequest: RegisterRequest): Response<MessageResponse> {
        return withContext(Dispatchers.IO) {
            apiService.signup(registerRequest)
        }
    }

    fun logout() {
        sharedPreferencesManager.clear()
        // Potentially notify other parts of the app or clear local database if needed
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferencesManager.getAuthToken() != null
    }

    fun getAuthToken(): String? {
        return sharedPreferencesManager.getAuthToken()
    }

    fun getUserId(): Long? {
        return sharedPreferencesManager.getUserId()
    }
}