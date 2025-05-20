package com.example.expensetracker.api

import com.example.expensetracker.api.models.request.ExpenseRequest
import com.example.expensetracker.api.models.request.LoginRequest
import com.example.expensetracker.api.models.request.RegisterRequest
import com.example.expensetracker.api.models.response.ExpenseResponse
import com.example.expensetracker.api.models.response.LoginResponse
import com.example.expensetracker.api.models.response.MessageResponse
import com.example.expensetracker.api.models.response.SummaryResponse
import retrofit2.Response // Important: Use retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Authentication
    @POST("auth/signup") // Relative to BASE_URL's /api/
    suspend fun signup(@Body registerRequest: RegisterRequest): Response<MessageResponse>

    @POST("auth/signin")
    suspend fun signin(@Body loginRequest: LoginRequest): Response<LoginResponse>

    // Expenses
    // Token will be added by AuthInterceptor, no need for @Header here anymore
    @GET("expenses")
    suspend fun getAllExpenses(): Response<List<ExpenseResponse>>

    @POST("expenses")
    suspend fun createExpense(@Body expenseRequest: ExpenseRequest): Response<ExpenseResponse>

    @GET("expenses/{id}")
    suspend fun getExpenseById(@Path("id") id: Long): Response<ExpenseResponse>

    @PUT("expenses/{id}")
    suspend fun updateExpense(@Path("id") id: Long, @Body expenseRequest: ExpenseRequest): Response<ExpenseResponse>

    @DELETE("expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: Long): Response<Unit> // Or specific success response

    @GET("expenses/filter")
    suspend fun filterExpenses(
        @Query("category") category: String?,
        @Query("startDate") startDate: String?, // Format: YYYY-MM-DD
        @Query("endDate") endDate: String?     // Format: YYYY-MM-DD
    ): Response<List<ExpenseResponse>>

    @GET("expenses/today")
    suspend fun getTodaysExpenses(): Response<List<ExpenseResponse>>

    @GET("expenses/week")
    suspend fun getThisWeeksExpenses(): Response<List<ExpenseResponse>>

    @GET("expenses/month")
    suspend fun getThisMonthsExpenses(): Response<List<ExpenseResponse>>

    @GET("expenses/summary")
    suspend fun getExpenseSummary(
        @Query("startDate") startDate: String, // Format: YYYY-MM-DD
        @Query("endDate") endDate: String      // Format: YYYY-MM-DD
    ): Response<SummaryResponse>
}