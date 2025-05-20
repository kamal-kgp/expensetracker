package com.example.expensetracker.ui.expenses.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R // For R.string access
import com.example.expensetracker.api.models.response.ExpenseResponse // Using API response model directly for now
import com.example.expensetracker.databinding.ItemExpenseBinding
import com.example.expensetracker.utils.CurrencyFormatter
import com.example.expensetracker.utils.DateUtils
import com.example.expensetracker.utils.Constants

class ExpenseAdapter(private val onExpenseClicked: (ExpenseResponse) -> Unit) :
    ListAdapter<ExpenseResponse, ExpenseAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val currentExpense = getItem(position)
        holder.bind(currentExpense)
        holder.itemView.setOnClickListener {
            onExpenseClicked(currentExpense)
        }
    }

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: ExpenseResponse) {
            binding.tvExpenseTitle.text = expense.title
            binding.tvExpenseAmount.text = CurrencyFormatter.formatAmount(expense.amount, expense.currency)
            binding.tvExpenseCategory.text = expense.category.replace("_", " ").capitalizeWords()


            val parsedDate = DateUtils.parseDate(expense.date, Constants.API_DATE_FORMAT)
            binding.tvExpenseDate.text = if (parsedDate != null) {
                DateUtils.formatDate(parsedDate, Constants.DISPLAY_DATE_FORMAT)
            } else {
                expense.date // fallback to raw date if parsing fails
            }

            // Example: Set category icon based on category name (expand this logic)
            when (expense.category.uppercase()) {
                "FOOD" -> binding.tvExpenseCategory.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_category_food_placeholder, 0, 0, 0) // Create these
                "TRANSPORTATION" -> binding.tvExpenseCategory.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_category_transport_placeholder, 0, 0, 0)
                // Add more categories
                else -> binding.tvExpenseCategory.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_category_placeholder, 0, 0, 0)
            }
        }
    }

    class ExpenseDiffCallback : DiffUtil.ItemCallback<ExpenseResponse>() {
        override fun areItemsTheSame(oldItem: ExpenseResponse, newItem: ExpenseResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExpenseResponse, newItem: ExpenseResponse): Boolean {
            return oldItem == newItem
        }
    }
}

// Helper extension function
fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.lowercase().replaceFirstChar(Char::titlecase) }

// Create placeholder drawables like ic_category_food_placeholder.xml, ic_category_transport_placeholder.xml etc.
// Example: app/src/main/res/drawable/ic_category_food_placeholder.xml
/*
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="16dp"
    android:height="16dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?android:attr/textColorSecondary">
  <path
      android:fillColor="@android:color/white"
      android:pathData="M21,3L3,3c-1.1,0 -2,0.9 -2,2v14c0,1.1 0.9,2 2,2h18c1.1,0 2,-0.9 2,-2L23,5c0,-1.1 -0.9,-2 -2,-2zM21,19L3,19v-8h18v8zM21,9L3,9L3,5h18v4z"/>
</vector>
*/