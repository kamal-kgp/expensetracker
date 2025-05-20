package com.example.expensetracker.api.models.request

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)