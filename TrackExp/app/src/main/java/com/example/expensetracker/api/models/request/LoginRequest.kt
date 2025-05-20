package com.example.expensetracker.api.models.request

data class LoginRequest(
    val username: String,
    val password: String
)