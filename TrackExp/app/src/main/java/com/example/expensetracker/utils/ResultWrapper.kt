package com.example.expensetracker.utils

// Helper Sealed Class for Result Handling
sealed class ResultWrapper<out T> {
    data class Success<out T>(val data: T) : ResultWrapper<T>()
    data class Error(val message: String, val code: Int? = null) : ResultWrapper<Nothing>()
    object Loading : ResultWrapper<Nothing>()
}