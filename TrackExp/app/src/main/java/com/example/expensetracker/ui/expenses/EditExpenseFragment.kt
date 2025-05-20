package com.example.expensetracker.ui.expenses

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.expensetracker.R
import com.example.expensetracker.api.models.request.ExpenseRequest
import com.example.expensetracker.api.models.response.ExpenseResponse
import com.example.expensetracker.utils.DateUtils
import com.example.expensetracker.utils.NetworkUtils
import com.example.expensetracker.utils.ResultWrapper // Ensure this import is present
import java.util.Calendar

// Inherits from AddExpenseFragment (which includes binding, viewModel, common UI setup methods)
class EditExpenseFragment : AddExpenseFragment() {

    private val args: EditExpenseFragmentArgs by navArgs() // Safe Args delegate
    private var expenseId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        expenseId = args.expenseId // Get expenseId from Safe Args
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Call super.onViewCreated FIRST to setup common elements like spinners, date picker logic from AddExpenseFragment.
        super.onViewCreated(view, savedInstanceState)

        // Then, customize for Edit mode
        // (activity as? MainActivity)?.supportActionBar?.title = "Edit Expense" // Title set by NavController in MainActivity
        binding.btnSaveExpense.text = getString(R.string.update_expense) // Default text for save button in AddEdit layout
        // The actual click listener is overridden in setupSaveButton() below.

        if (expenseId != -1L) {
            // Corrected logic to check if fetching existing expense data is needed:
            val currentResult = expenseViewModel.singleExpense.value
            val currentlyLoadedData = (currentResult as? ResultWrapper.Success<ExpenseResponse>)?.data

            if (currentlyLoadedData?.id != expenseId) {
                // Fetch if:
                // 1. Nothing is successfully loaded yet for this expense (currentlyLoadedData is null).
                // 2. Or, the successfully loaded data is for a different expenseId.
                // We also check if it's not already loading to prevent redundant calls.
                if (currentResult !is ResultWrapper.Loading) {
                    expenseViewModel.fetchExpenseById(expenseId)
                }
                // If currentResult is ResultWrapper.Loading, we assume it's fetching.
                // The observer will handle the outcome.
            } else if (currentlyLoadedData != null) {
                // Data for the correct expenseId is already successfully loaded.
                // Populate fields directly if the view is being recreated and observer might not re-trigger.
                // The observer will also call populateFields, this is a belt-and-suspenders approach for existing data.
                populateFields(currentlyLoadedData)
            }
        } else {
            Toast.makeText(context, "Error: Expense ID not found.", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
            return // Exit if no valid ID
        }

        // Observer for fetching the single expense to edit (this is crucial)
        expenseViewModel.singleExpense.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultWrapper.Loading -> {
                    binding.progressBarAddEdit.isVisible = true // Show progress for loading details
                }
                is ResultWrapper.Success -> {
                    // Only populate if the success data is for the current expenseId
                    if (result.data.id == expenseId) {
                        binding.progressBarAddEdit.isVisible = false
                        populateFields(result.data)
                    }
                }
                is ResultWrapper.Error -> {
                    binding.progressBarAddEdit.isVisible = false
                    Toast.makeText(context, "Error fetching expense details: ${result.message}", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack() // Optionally navigate back on error
                }
            }
        }
        // The observer for expenseOperationResult (for save/update) is inherited from AddExpenseFragment
        // and will trigger when updateExpense is called.
    }

    private fun populateFields(expense: ExpenseResponse) {
        binding.etExpenseTitle.setText(expense.title)
        binding.etExpenseAmount.setText(expense.amount.toString())
        binding.actvExpenseCurrency.setText(expense.currency, false) // false to prevent filtering
        binding.actvExpenseCategory.setText(expense.category, false) // false to prevent filtering

        DateUtils.parseDate(expense.date, com.example.expensetracker.utils.Constants.API_DATE_FORMAT)?.let { date ->
            val calendar = Calendar.getInstance()
            calendar.time = date
            super.updateDateInView(date) // Call parent's method to set text in etExpenseDate
            // super.selectedDate = calendar // If AddExpenseFragment's selectedDate needs to be synced
        }
    }

    override fun setupSaveButton() { // Override to call updateExpense
        binding.btnSaveExpense.text = getString(R.string.update_expense) // Ensure button text is correct
        binding.btnSaveExpense.setOnClickListener {
            if (validateInput()) { // validateInput is inherited from AddExpenseFragment
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
                // Call updateExpense from ViewModel
                expenseViewModel.updateExpense(expenseId, expenseRequest)
            }
        }
    }
    // Other methods like setupSpinners, setupDatePicker, validateInput, and setupObservers (for expenseOperationResult)
    // are inherited from AddExpenseFragment. The setupObservers for expenseOperationResult
    // will correctly handle the LiveData updates from updateExpense.
}