package com.example.expensetracker.api.models.response

import com.google.gson.annotations.SerializedName

data class ExpenseResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("date")
    val date: String, // Format: "YYYY-MM-DD"
    @SerializedName("userId")
    val userId: Long
)