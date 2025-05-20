package com.example.expensetracker.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = false) // Assuming ID comes from server, or true if purely local
    val id: Long,
    val title: String,
    val amount: Double,
    val currency: String,
    val category: String,
    val date: String, // Store as ISO String "YYYY-MM-DD" or Long (timestamp)
    val userId: Long, // To associate with a user if supporting multiple local users
    var isSynced: Boolean = true // Example field for sync status if doing offline first
)