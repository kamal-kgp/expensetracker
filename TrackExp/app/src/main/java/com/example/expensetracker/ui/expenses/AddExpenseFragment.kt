package com.example.expensetracker.ui.expenses

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.expensetracker.R
import com.example.expensetracker.api.models.request.ExpenseRequest
import com.example.expensetracker.databinding.FragmentAddEditExpenseBinding
import com.example.expensetracker.utils.Constants
import com.example.expensetracker.utils.DateUtils
import com.example.expensetracker.utils.NetworkUtils
import com.example.expensetracker.utils.ResultWrapper
import com.example.expensetracker.viewmodel.ExpenseViewModel
import java.util.Calendar
import java.util.Date

open class AddExpenseFragment : Fragment() {

    private var _binding: FragmentAddEditExpenseBinding? = null
    protected val binding get() = _binding!!

    protected val expenseViewModel: ExpenseViewModel by activityViewModels()
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()
        setupDatePicker()
        setupSaveButton()
        setupObservers()

        // Set current date by default
        updateDateInView(selectedDate.time)
        (activity as? MainActivity)?.supportActionBar?.title = "Add Expense"
    }

    private fun setupSpinners() {
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, Constants.EXPENSE_CATEGORIES)
        binding.actvExpenseCategory.setAdapter(categoryAdapter)
        if (Constants.EXPENSE_CATEGORIES.isNotEmpty()) {
            binding.actvExpenseCategory.setText(Constants.EXPENSE_CATEGORIES[0], false) // Default
        }

        val currencyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, Constants.CURRENCIES)
        binding.actvExpenseCurrency.setAdapter(currencyAdapter)
        if (Constants.CURRENCIES.isNotEmpty()) {
            binding.actvExpenseCurrency.setText(Constants.CURRENCIES[0], false) // Default to INR or first
        }
    }

    private fun setupDatePicker() {
        binding.etExpenseDate.setOnClickListener {
            showDatePickerDialog()
        }
        binding.tilExpenseDate.setEndIconOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val year = selectedDate.get(Calendar.YEAR)
        val month = selectedDate.get(Calendar.MONTH)
        val day = selectedDate.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)
            updateDateInView(selectedDate.time)
        }, year, month, day).show()
    }

    protected fun updateDateInView(date: Date) {
        binding.etExpenseDate.setText(DateUtils.formatDate(date, Constants.API_DATE_FORMAT))
    }

    protected open fun setupSaveButton() {
        binding.btnSaveExpense.setOnClickListener {
            if (validateInput()) {
                if (!NetworkUtils.isNetworkAvailable(requireContext())) {
                    Toast.makeText(context, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val expenseRequest = ExpenseRequest(
                    title = binding.etExpenseTitle.text.toString().trim(),
                    amount = binding.etExpenseAmount.text.toString().toDouble(),
                    currency = binding.actvExpenseCurrency.text.toString(),
                    category = binding.actvExpenseCategory.text.toString(),
                    date = binding.etExpenseDate.text.toString()
                )
                expenseViewModel.createExpense(expenseRequest)
            }
        }
    }

    protected open fun setupObservers() {
        expenseViewModel.expenseOperationResult.observe(viewLifecycleOwner) { result ->
            // Handle the null case if clearExpenseOperationResult() sets the value to null
            if (result == null) {
                // This means the event was cleared, ensure UI is in a neutral state
                // if it was previously loading due to this LiveData.
                binding.progressBarAddEdit.isVisible = false
                binding.btnSaveExpense.isEnabled = true
                return@observe
            }

            when (result) {
                is ResultWrapper.Loading -> {
                    binding.progressBarAddEdit.isVisible = true
                    binding.btnSaveExpense.isEnabled = false
                }
                is ResultWrapper.Success -> {
                    binding.progressBarAddEdit.isVisible = false
                    binding.btnSaveExpense.isEnabled = true
                    if (result.data) { // result.data is Boolean (true for success)
                        Toast.makeText(context, "Expense saved successfully!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack() // Go back to list
                    }
                    expenseViewModel.clearExpenseOperationResult()
                }
                is ResultWrapper.Error -> {
                    binding.progressBarAddEdit.isVisible = false
                    binding.btnSaveExpense.isEnabled = true
                    Toast.makeText(context, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                    expenseViewModel.clearExpenseOperationResult()
                }
            }
        }
    }

    protected fun validateInput(): Boolean {
        var isValid = true
        if (binding.etExpenseTitle.text.isNullOrBlank()) {
            binding.tilExpenseTitle.error = getString(R.string.required_field)
            isValid = false
        } else {
            binding.tilExpenseTitle.error = null
        }

        val amountStr = binding.etExpenseAmount.text.toString()
        if (amountStr.isBlank()) {
            binding.tilExpenseAmount.error = getString(R.string.required_field)
            isValid = false
        } else {
            try {
                amountStr.toDouble()
                binding.tilExpenseAmount.error = null
            } catch (e: NumberFormatException) {
                binding.tilExpenseAmount.error = getString(R.string.invalid_amount)
                isValid = false
            }
        }

        if (binding.actvExpenseCurrency.text.isNullOrBlank()) {
            binding.tilExpenseCurrency.error = getString(R.string.required_field) // Or select default
            isValid = false
        } else {
            binding.tilExpenseCurrency.error = null
        }


        if (binding.actvExpenseCategory.text.isNullOrBlank()) {
            binding.tilExpenseCategory.error = getString(R.string.required_field) // Or select default
            isValid = false
        } else {
            binding.tilExpenseCategory.error = null
        }

        if (binding.etExpenseDate.text.isNullOrBlank()) {
            binding.tilExpenseDate.error = getString(R.string.required_field)
            isValid = false
        } else {
            binding.tilExpenseDate.error = null
        }
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Clear any specific LiveData observers if they were set to only observe specific states
        // For expenseOperationResult, it's fine as it's a general indicator.
    }
}