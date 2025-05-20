package com.example.expensetracker.utils

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyFormatter {
    fun formatAmount(amount: Double, currencyCode: String): String {
        return try {
            val currency = Currency.getInstance(currencyCode.uppercase(Locale.ROOT))
            val format = NumberFormat.getCurrencyInstance(Locale.getDefault()) // Or a specific locale
            format.currency = currency
            format.format(amount)
        } catch (e: IllegalArgumentException) {
            // Fallback if currency code is invalid
            "${"%.2f".format(amount)} $currencyCode"
        }
    }
}