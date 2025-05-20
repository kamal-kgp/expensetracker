package com.example.expensetracker.api.models.response

import com.google.gson.annotations.SerializedName

data class SummaryResponse(
    @SerializedName("total")
    val total: Double,
    @SerializedName("categorySummary")
    val categorySummary: Map<String, Double>
)