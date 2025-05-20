package com.example.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.api.models.request.LoginRequest
import com.example.expensetracker.api.models.request.RegisterRequest
import com.example.expensetracker.api.models.response.LoginResponse
import com.example.expensetracker.api.models.response.MessageResponse
import com.example.expensetracker.data.repository.AuthRepository
import com.example.expensetracker.utils.ResultWrapper
import kotlinx.coroutines.launch


class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)

    private val _loginResult = MutableLiveData<ResultWrapper<LoginResponse>>()
    val loginResult: LiveData<ResultWrapper<LoginResponse>> = _loginResult

    private val _registerResult = MutableLiveData<ResultWrapper<MessageResponse>>()
    val registerResult: LiveData<ResultWrapper<MessageResponse>> = _registerResult

    fun login(loginRequest: LoginRequest) {
        _loginResult.value = ResultWrapper.Loading
        viewModelScope.launch {
            try {
                val response = authRepository.login(loginRequest)
                if (response.isSuccessful && response.body() != null) {
                    _loginResult.postValue(ResultWrapper.Success(response.body()!!))
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Login failed"
                    _loginResult.postValue(ResultWrapper.Error(errorMsg, response.code()))
                }
            } catch (e: Exception) {
                _loginResult.postValue(ResultWrapper.Error(e.message ?: "An unknown error occurred"))
            }
        }
    }

    fun register(registerRequest: RegisterRequest) {
        _registerResult.value = ResultWrapper.Loading
        viewModelScope.launch {
            try {
                val response = authRepository.register(registerRequest)
                if (response.isSuccessful && response.body() != null) {
                    _registerResult.postValue(ResultWrapper.Success(response.body()!!))
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Registration failed"
                    _registerResult.postValue(ResultWrapper.Error(errorMsg, response.code()))
                }
            } catch (e: Exception) {
                _registerResult.postValue(ResultWrapper.Error(e.message ?: "An unknown error occurred"))
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }

    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
}