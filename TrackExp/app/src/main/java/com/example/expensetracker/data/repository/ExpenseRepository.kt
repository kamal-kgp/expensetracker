package com.example.expensetracker.data.repository

import android.content.Context
import com.example.expensetracker.api.ApiService
import com.example.expensetracker.api.RetrofitClient
import com.example.expensetracker.api.models.request.ExpenseRequest
import com.example.expensetracker.api.models.response.ExpenseResponse
import com.example.expensetracker.api.models.response.SummaryResponse
// import com.example.expensetracker.data.local.database.ExpenseDao // If using Room for caching
// import com.example.expensetracker.data.local.database.ExpenseDatabase // If using Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ExpenseRepository(applicationContext: Context) {

    private val apiService: ApiService =
        RetrofitClient.getClient(applicationContext).create(ApiService::class.java)
    // private val expenseDao: ExpenseDao = ExpenseDatabase.getDatabase(applicationContext).expenseDao() // Uncomment if using Room

    suspend fun getAllExpenses(): Response<List<ExpenseResponse>> {
        return withContext(Dispatchers.IO) {
            // Optional: Fetch from DAO first, then network, then update DAO
            apiService.getAllExpenses()
        }
    }

    suspend fun createExpense(expenseRequest: ExpenseRequest): Response<ExpenseResponse> {
        return withContext(Dispatchers.IO) {
            apiService.createExpense(expenseRequest)
            // Optional: Save to DAO after successful creation
        }
    }

    suspend fun getExpenseById(id: Long): Response<ExpenseResponse> {
        return withContext(Dispatchers.IO) {
            apiService.getExpenseById(id)
        }
    }

    suspend fun updateExpense(id: Long, expenseRequest: ExpenseRequest): Response<ExpenseResponse> {
        return withContext(Dispatchers.IO) {
            apiService.updateExpense(id, expenseRequest)
            // Optional: Update in DAO after successful update
        }
    }

    suspend fun deleteExpense(id: Long): Response<Unit> {
        return withContext(Dispatchers.IO) {
            apiService.deleteExpense(id)
            // Optional: Delete from DAO after successful deletion
        }
    }

    suspend fun filterExpenses(
        category: String?,
        startDate: String?,
        endDate: String?
    ): Response<List<ExpenseResponse>> {
        return withContext(Dispatchers.IO) {
            apiService.filterExpenses(category, startDate, endDate)
        }
    }

    suspend fun getTodaysExpenses(): Response<List<ExpenseResponse>> {
        return withContext(Dispatchers.IO) {
            apiService.getTodaysExpenses()
        }
    }

    suspend fun getThisWeeksExpenses(): Response<List<ExpenseResponse>> {
        return withContext(Dispatchers.IO) {
            apiService.getThisWeeksExpenses()
        }
    }

    suspend fun getThisMonthsExpenses(): Response<List<ExpenseResponse>> {
        return withContext(Dispatchers.IO) {
            apiService.getThisMonthsExpenses()
        }
    }

    suspend fun getExpenseSummary(startDate: String, endDate: String): Response<SummaryResponse> {
        return withContext(Dispatchers.IO) {
            apiService.getExpenseSummary(startDate, endDate)
        }
    }
}