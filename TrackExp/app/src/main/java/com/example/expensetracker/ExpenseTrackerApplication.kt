package com.example.expensetracker

import android.app.Application

// If you were using Hilt or Koin, you would initialize it here.
// For now, it's basic. Can be used to get applicationContext if needed globally.
class ExpenseTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide singletons or libraries here if needed
        // For example, if SharedPreferencesManager needed early init or if using DI.
    }
}