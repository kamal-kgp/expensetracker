package com.example.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.api.models.request.ExpenseRequest
import com.example.expensetracker.api.models.response.ExpenseResponse
import com.example.expensetracker.api.models.response.SummaryResponse
import com.example.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.launch
import com.example.expensetracker.utils.ResultWrapper


class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseRepository = ExpenseRepository(application)

    private val _expenseList = MutableLiveData<ResultWrapper<List<ExpenseResponse>>>()
    val expenseList: LiveData<ResultWrapper<List<ExpenseResponse>>> = _expenseList

    private val _singleExpense = MutableLiveData<ResultWrapper<ExpenseResponse>>()
    val singleExpense: LiveData<ResultWrapper<ExpenseResponse>> = _singleExpense

    private val _expenseOperationResult = MutableLiveData<ResultWrapper<Boolean>?>() // True for success
    val expenseOperationResult: LiveData<ResultWrapper<Boolean>?> = _expenseOperationResult

    private val _expenseSummary = MutableLiveData<ResultWrapper<SummaryResponse>>()
    val expenseSummary: LiveData<ResultWrapper<SummaryResponse>> = _expenseSummary


    fun fetchAllExpenses() {
        _expenseList.value = ResultWrapper.Loading
        viewModelScope.launch {
            try {
                val response = expenseRepository.getAllExpenses()
                if (response.isSuccessful && response.body() != null) {
                    _expenseList.postValue(ResultWrapper.Success(response.body()!!))
                } else {
                    _expenseList.postValue(ResultWrapper.Error(response.errorBody()?.string() ?: "Failed to fetch expenses", response.code()))
                }
            } catch (e: Exception) {
                _expenseList.postValue(ResultWrapper.Error(e.message ?: "Network error"))
            }
        }
    }

    fun createExpense(expenseRequest: ExpenseRequest) {
        _expenseOperationResult.value = ResultWrapper.Loading
        viewModelScope.launch {
            try {
                val response = expenseRepository.createExpense(expenseRequest)
                if (response.isSuccessful) {
                    _expenseOperationResult.postValue(ResultWrapper.Success(true))
                } else {
                    _expenseOperationResult.postValue(ResultWrapper.Error(response.errorBody()?.string() ?: "Failed to create expense", response.code()))
                }
            } catch (e: Exception) {
                _expenseOperationResult.postValue(ResultWrapper.Error(e.message ?: "Network error"))
            }
        }
    }

    fun fetchExpenseById(id: Long) {
        _singleExpense.value = ResultWrapper.Loading
        viewModelScope.launch {
            try {
                val response = expenseRepository.getExpenseById(id)
                if (response.isSuccessful && response.body() != null) {
                    _singleExpense.postValue(ResultWrapper.Success(response.body()!!))
                } else {
                    _singleExpense.postValue(ResultWrapper.Error(response.errorBody()?.string() ?: "Failed to fetch expense details", response.code()))
                }
            } catch (e: Exception) {
                _singleExpense.postValue(ResultWrapper.Error(e.message ?: "Network error"))
            }
        }
    }

    fun updateExpense(id: Long, expenseRequest: ExpenseRequest) {
        _expenseOperationResult.value = ResultWrapper.Loading
        viewModelScope.launch {
            try {
                val response = expenseRepository.updateExpense(id, expenseRequest)
                if (response.isSuccessful) {
                    _expenseOperationResult.postValue(ResultWrapper.Success(true))
                } else {
                    _expenseOperationResult.postValue(ResultWrapper.Error(response.errorBody()?.string() ?: "Failed to update expense", response.code()))
                }
            } catch (e: Exception) {
                _expenseOperationResult.postValue(ResultWrapper.Error(e.message ?: "Network error"))
            }
        }
    }

    fun deleteExpense(id: Long) {
        _expenseOperationResult.value = ResultWrapper.Loading
        viewModelScope.launch {
            try {
                val response = expenseRepository.deleteExpense(id)
                if (response.isSuccessful) { // Unit response for delete might be just success
                    _expenseOperationResult.postValue(ResultWrapper.Success(true))
                } else {
                    _expenseOperationResult.postValue(ResultWrapper.Error(response.errorBody()?.string() ?: "Failed to delete expense", response.code()))
                }
            } catch (e: Exception) {
                _expenseOperationResult.postValue(ResultWrapper.Error(e.message ?: "Network error"))
            }
        }
    }

    fun filterExpenses(category: String?, startDate: String?, endDate: String?) {
        _expenseList.value = ResultWrapper.Loading
        viewModelScope.launch {
            try {
                val response = expenseRepository.filterExpenses(category, startDate, endDate)
                if (response.isSuccessful && response.body() != null) {
                    _expenseList.postValue(ResultWrapper.Success(response.body()!!))
                } else {
                    _expenseList.postValue(ResultWrapper.Error(response.errorBody()?.string() ?: "Failed to filter expenses", response.code()))
                }
            } catch (e: Exception) {
                _expenseList.postValue(ResultWrapper.Error(e.message ?: "Network error"))
            }
        }
    }

    fun fetchTodaysExpenses() {
        _expenseList.value = ResultWrapper.Loading
        viewModelScope.launch {
            try {
                val response = expenseRepository.getTodaysExpenses()
                if (response.isSuccessful && response.body() != null) {
                    _expenseList.postValue(ResultWrapper.Success(response.body()!!))
                } else {
                    _expenseList.postValue(ResultWrapper.Error(response.errorBody()?.string() ?: "Failed to fetch today's expenses", response.code()))
                }
            } catch (e: Exception) {
                _expenseList.postValue(ResultWrapper.Error(e.message ?: "Network error"))
            }
        }
    }

    fun fetchThisWeeksExpenses() {
        _expenseList.value = ResultWrapper.Loading
        viewModelScope.launch {
            try {
                val response = expenseRepository.getThisWeeksExpenses()
                if (response.isSuccessful && response.body() != null) {
                    _expenseList.postValue(ResultWrapper.Success(response.body()!!))
                } else {
                    _expenseList.postValue(ResultWrapper.Error(response.errorBody()?.string() ?: "Failed to fetch week's expenses", response.code()))
                }
            } catch (e: Exception) {
                _expenseList.postValue(ResultWrapper.Error(e.message ?: "Network error"))
            }
        }
    }

    fun fetchThisMonthsExpenses() {
        _expenseList.value = ResultWrapper.Loading
        viewModelScope.launch {
            try {
                val response = expenseRepository.getThisMonthsExpenses()
                if (response.isSuccessful && response.body() != null) {
                    _expenseList.postValue(ResultWrapper.Success(response.body()!!))
                } else {
                    _expenseList.postValue(ResultWrapper.Error(response.errorBody()?.string() ?: "Failed to fetch month's expenses", response.code()))
                }
            } catch (e: Exception) {
                _expenseList.postValue(ResultWrapper.Error(e.message ?: "Network error"))
            }
        }
    }

    fun fetchExpenseSummary(startDate: String, endDate: String) {
        _expenseSummary.value = ResultWrapper.Loading
        viewModelScope.launch {
            try {
                val response = expenseRepository.getExpenseSummary(startDate, endDate)
                if (response.isSuccessful && response.body() != null) {
                    _expenseSummary.postValue(ResultWrapper.Success(response.body()!!))
                } else {
                    _expenseSummary.postValue(ResultWrapper.Error(response.errorBody()?.string() ?: "Failed to fetch summary", response.code()))
                }
            } catch (e: Exception) {
                _expenseSummary.postValue(ResultWrapper.Error(e.message ?: "Network error"))
            }
        }
    }

    fun clearExpenseOperationResult() {
        _expenseOperationResult.value = null // Or set to an initial/idle state if null is not desired
    }
}