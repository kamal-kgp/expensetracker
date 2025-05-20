package com.example.expensetracker.utils

object Constants {
    const val API_DATE_FORMAT = "yyyy-MM-dd"
    const val DISPLAY_DATE_FORMAT = "MMM dd, yyyy" // Example display format

    const val ARG_EXPENSE_ID = "expense_id"

    // Categories - Consider making this dynamic or an enum
    val EXPENSE_CATEGORIES = listOf("FOOD", "TRANSPORTATION", "SHOPPING", "UTILITIES", "HEALTHCARE", "ENTERTAINMENT", "OTHER")
    val CURRENCIES = listOf("INR", "USD", "EUR") // Example, could be more dynamic
}