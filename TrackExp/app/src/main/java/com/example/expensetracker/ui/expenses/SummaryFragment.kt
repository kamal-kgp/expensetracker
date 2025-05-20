package com.example.expensetracker.ui.expenses

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.expensetracker.R
import com.example.expensetracker.api.models.response.SummaryResponse
import com.example.expensetracker.databinding.FragmentSummaryBinding
import com.example.expensetracker.utils.Constants
import com.example.expensetracker.utils.CurrencyFormatter
import com.example.expensetracker.utils.DateUtils
import com.example.expensetracker.utils.NetworkUtils
import com.example.expensetracker.utils.ResultWrapper
import com.example.expensetracker.viewmodel.ExpenseViewModel
import java.util.Calendar
import java.util.Date

class SummaryFragment : Fragment() {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!

    private val expenseViewModel: ExpenseViewModel by activityViewModels()
    private var selectedStartDate: Calendar = Calendar.getInstance()
    private var selectedEndDate: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.supportActionBar?.title = getString(R.string.expense_summary)

        // Default dates (e.g., start and end of current month)
        selectedStartDate.set(Calendar.DAY_OF_MONTH, 1)
        updateDateInView(binding.etSummaryStartDate, selectedStartDate.time)
        // selectedEndDate is already current date, which is fine for end of month if current date is last day
        // Or explicitly set to last day of month:
        // selectedEndDate.set(Calendar.DAY_OF_MONTH, selectedEndDate.getActualMaximum(Calendar.DAY_OF_MONTH))
        updateDateInView(binding.etSummaryEndDate, selectedEndDate.time)


        setupDatePickers()
        setupFetchButton()
        setupObservers()

        // Initially hide summary details until fetched
        binding.tvTotalExpenses.visibility = View.GONE
        binding.tvCategorySummaryTitle.visibility = View.GONE
        binding.tvNoSummaryData.isVisible = true
    }

    private fun setupDatePickers() {
        binding.etSummaryStartDate.setOnClickListener { showDatePickerDialog(true) }
        binding.tilSummaryStartDate.setEndIconOnClickListener { showDatePickerDialog(true) }

        binding.etSummaryEndDate.setOnClickListener { showDatePickerDialog(false) }
        binding.tilSummaryEndDate.setEndIconOnClickListener { showDatePickerDialog(false) }
    }

    private fun showDatePickerDialog(isStartDate: Boolean) {
        val calendar = if (isStartDate) selectedStartDate else selectedEndDate

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val tempCal = Calendar.getInstance()
            tempCal.set(selectedYear, selectedMonth, selectedDayOfMonth)
            if (isStartDate) {
                selectedStartDate = tempCal
                updateDateInView(binding.etSummaryStartDate, tempCal.time)
            } else {
                selectedEndDate = tempCal
                updateDateInView(binding.etSummaryEndDate, tempCal.time)
            }
        }, year, month, day).show()
    }

    private fun updateDateInView(editText: android.widget.EditText, date: Date) {
        editText.setText(DateUtils.formatDate(date, Constants.API_DATE_FORMAT))
    }

    private fun setupFetchButton() {
        binding.btnFetchSummary.setOnClickListener {
            val startDateStr = binding.etSummaryStartDate.text.toString()
            val endDateStr = binding.etSummaryEndDate.text.toString()

            if (startDateStr.isBlank() || endDateStr.isBlank()) {
                Toast.makeText(context, "Please select both start and end dates.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedStartDate.after(selectedEndDate)) {
                Toast.makeText(context, "Start date cannot be after end date.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!NetworkUtils.isNetworkAvailable(requireContext())) {
                Toast.makeText(context, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            expenseViewModel.fetchExpenseSummary(startDateStr, endDateStr)
        }
    }

    private fun setupObservers() {
        expenseViewModel.expenseSummary.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultWrapper.Loading -> {
                    binding.progressBarSummary.isVisible = true
                    binding.tvTotalExpenses.visibility = View.GONE
                    binding.tvCategorySummaryTitle.visibility = View.GONE
                    binding.llCategorySummaryContainer.removeAllViews()
                    binding.tvNoSummaryData.isVisible = false
                }
                is ResultWrapper.Success -> {
                    binding.progressBarSummary.isVisible = false
                    binding.tvNoSummaryData.isVisible = false
                    displaySummary(result.data)
                }
                is ResultWrapper.Error -> {
                    binding.progressBarSummary.isVisible = false
                    binding.tvTotalExpenses.visibility = View.GONE
                    binding.tvCategorySummaryTitle.visibility = View.GONE
                    binding.llCategorySummaryContainer.removeAllViews()
                    binding.tvNoSummaryData.text = "Error: ${result.message}"
                    binding.tvNoSummaryData.isVisible = true
                    Toast.makeText(context, "Error fetching summary: ${result.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun displaySummary(summary: SummaryResponse) {
        binding.tvTotalExpenses.text = getString(R.string.total_expenses, CurrencyFormatter.formatAmount(summary.total, "INR")) // Assuming INR for now
        binding.tvTotalExpenses.visibility = View.VISIBLE
        binding.tvCategorySummaryTitle.visibility = View.VISIBLE

        binding.llCategorySummaryContainer.removeAllViews() // Clear previous entries

        if (summary.categorySummary.isEmpty()) {
            val noCategoryData = TextView(context).apply {
                text = "No category breakdown available."
                setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Body2)
            }
            binding.llCategorySummaryContainer.addView(noCategoryData)
        } else {
            summary.categorySummary.forEach { (category, amount) ->
                val categoryView = TextView(context).apply {
                    text = "${category.capitalizeWords()}: ${CurrencyFormatter.formatAmount(amount, "INR")}"
                    setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Body1)
                    setPadding(0, 4, 0, 4)
                }
                binding.llCategorySummaryContainer.addView(categoryView)
            }
        }
    }
    // Helper extension from ExpenseAdapter/DetailFragment
    fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.lowercase().replaceFirstChar(Char::titlecase) }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}