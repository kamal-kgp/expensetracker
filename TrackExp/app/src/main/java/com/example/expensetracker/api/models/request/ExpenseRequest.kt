package com.example.expensetracker.api.models.request

// Used for creating and updating expenses
data class ExpenseRequest(
    val title: String,
    val amount: Double,
    val currency: String,
    val category: String,
    val date: String // Format: "YYYY-MM-DD"
)