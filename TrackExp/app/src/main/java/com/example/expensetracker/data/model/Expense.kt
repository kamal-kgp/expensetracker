package com.example.expensetracker.data.model

// Domain model representing an expense within the app
// This might be identical to ExpenseResponse or a slightly different representation
data class Expense(
    val id: Long,
    val title: String,
    val amount: Double,
    val currency: String,
    val category: String,
    val date: String, // Format: "YYYY-MM-DD"
    val userId: Long
)

// Extension function to map from API response to domain model (if they differ significantly)
// fun com.example.expensetracker.api.models.response.ExpenseResponse.toDomain(): Expense {
//    return Expense(id = this.id, title = this.title, /* ... other fields ... */ userId = this.userId)
// }

// Extension function to map from Domain model to Entity for Room (if structure differs)
// fun Expense.toEntity(): com.example.expensetracker.data.local.database.entities.ExpenseEntity {
//    return com.example.expensetracker.data.local.database.entities.ExpenseEntity(id = this.id, /* ... */)
// }

// Extension function to map from Entity to Domain model
// fun com.example.expensetracker.data.local.database.entities.ExpenseEntity.toDomain(): Expense {
//    return Expense(id = this.id, /* ... */)
// }