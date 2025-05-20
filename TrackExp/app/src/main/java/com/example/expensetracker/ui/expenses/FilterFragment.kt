package com.example.expensetracker.ui.expenses

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.expensetracker.R
import com.example.expensetracker.databinding.FragmentFilterBinding
import com.example.expensetracker.utils.Constants
import com.example.expensetracker.utils.DateUtils
import com.example.expensetracker.viewmodel.ExpenseViewModel
import java.util.Calendar
import java.util.Date

class FilterFragment : DialogFragment() {

    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!

    private val expenseViewModel: ExpenseViewModel by activityViewModels()

    private var selectedStartDate: Calendar? = null
    private var selectedEndDate: Calendar? = null
    private var selectedCategory: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        dialog?.setTitle(getString(R.string.filter_expenses)) // Optional: if not using a custom title TextView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategorySpinner()
        setupDatePickers()

        binding.btnApplyFilter.setOnClickListener {
            val categoryToFilter = if (binding.actvFilterCategory.text.toString() == getString(R.string.all_categories) || binding.actvFilterCategory.text.isNullOrBlank()) {
                null
            } else {
                binding.actvFilterCategory.text.toString()
            }
            val startDateStr = selectedStartDate?.let { DateUtils.formatDate(it.time) }
            val endDateStr = selectedEndDate?.let { DateUtils.formatDate(it.time) }

            // Basic validation: if one date is set, the other should also ideally be set,
            // or ensure endDate is after startDate
            if ((startDateStr != null && endDateStr == null) || (startDateStr == null && endDateStr != null)) {
                Toast.makeText(context, "Please select both start and end dates, or clear them.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedStartDate != null && selectedEndDate != null && selectedStartDate!!.after(selectedEndDate)) {
                Toast.makeText(context, "Start date cannot be after end date.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            expenseViewModel.filterExpenses(categoryToFilter, startDateStr, endDateStr)
            dismiss()
        }

        binding.btnClearFilter.setOnClickListener {
            selectedStartDate = null
            selectedEndDate = null
            binding.etFilterStartDate.text = null
            binding.etFilterEndDate.text = null
            binding.actvFilterCategory.setText(getString(R.string.all_categories), false)
            // Optionally, trigger a fetchAllExpenses to clear filters
            expenseViewModel.fetchAllExpenses()
            dismiss()
        }
    }

    private fun setupCategorySpinner() {
        val categoriesWithAll = listOf(getString(R.string.all_categories)) + Constants.EXPENSE_CATEGORIES
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categoriesWithAll)
        binding.actvFilterCategory.setAdapter(categoryAdapter)
        binding.actvFilterCategory.setText(getString(R.string.all_categories), false) // Default to "All"
    }

    private fun setupDatePickers() {
        binding.etFilterStartDate.setOnClickListener {
            showDatePickerDialog(true)
        }
        binding.tilFilterStartDate.setEndIconOnClickListener {
            showDatePickerDialog(true)
        }

        binding.etFilterEndDate.setOnClickListener {
            showDatePickerDialog(false)
        }
        binding.tilFilterEndDate.setEndIconOnClickListener {
            showDatePickerDialog(false)
        }
    }

    private fun showDatePickerDialog(isStartDate: Boolean) {
        val calendar = if (isStartDate) {
            selectedStartDate ?: Calendar.getInstance()
        } else {
            selectedEndDate ?: Calendar.getInstance()
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val tempCal = Calendar.getInstance()
            tempCal.set(selectedYear, selectedMonth, selectedDayOfMonth)
            if (isStartDate) {
                selectedStartDate = tempCal
                updateDateInView(binding.etFilterStartDate, tempCal.time)
            } else {
                selectedEndDate = tempCal
                updateDateInView(binding.etFilterEndDate, tempCal.time)
            }
        }, year, month, day).show()
    }

    private fun updateDateInView(editText: android.widget.EditText, date: Date) {
        editText.setText(DateUtils.formatDate(date, Constants.API_DATE_FORMAT))
    }

    override fun onStart() {
        super.onStart()
        // Optional: Set dialog width
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}