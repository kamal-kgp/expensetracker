package com.example.expensetracker.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    fun formatDate(date: Date, format: String = Constants.API_DATE_FORMAT): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(date)
    }

    fun parseDate(dateString: String, format: String = Constants.API_DATE_FORMAT): Date? {
        return try {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            sdf.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getCurrentDateString(format: String = Constants.API_DATE_FORMAT): String {
        return formatDate(Calendar.getInstance().time, format)
    }

    // You might need more specific functions for "today", "this week", "this month"
    // For API calls, the server usually handles this logic based on the endpoint.
    // These could be for client-side display or logic if needed.

    fun getTodayRange(): Pair<String, String> {
        val today = Calendar.getInstance()
        val dateStr = formatDate(today.time)
        return Pair(dateStr, dateStr)
    }

    fun getThisWeekRange(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        // Set to the first day of the week (e.g., Sunday or Monday depending on locale)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val startDate = formatDate(calendar.time)

        // Set to the last day of the week
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endDate = formatDate(calendar.time)
        return Pair(startDate, endDate)
    }

    fun getThisMonthRange(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        // Set to the first day of the month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = formatDate(calendar.time)

        // Set to the last day of the month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endDate = formatDate(calendar.time)
        return Pair(startDate, endDate)
    }
}