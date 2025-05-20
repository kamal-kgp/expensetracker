package com.example.expensetracker.ui.expenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.expensetracker.R
import com.example.expensetracker.api.models.response.ExpenseResponse
import com.example.expensetracker.databinding.FragmentExpenseDetailBinding
import com.example.expensetracker.utils.CurrencyFormatter
import com.example.expensetracker.utils.DateUtils
import com.example.expensetracker.utils.NetworkUtils
import com.example.expensetracker.utils.ResultWrapper // Ensure this import is present
import com.example.expensetracker.viewmodel.ExpenseViewModel

class ExpenseDetailFragment : Fragment() {

    private var _binding: FragmentExpenseDetailBinding? = null
    private val binding get() = _binding!!

    private val expenseViewModel: ExpenseViewModel by activityViewModels()
    private val args: ExpenseDetailFragmentArgs by navArgs() // Safe Args delegate
    private var expenseId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        expenseId = args.expenseId // Get expenseId from Safe Args
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // The title is typically set by the NavController and AppBarConfiguration in MainActivity
        // (activity as? MainActivity)?.supportActionBar?.title = "Expense Detail"

        if (expenseId == -1L) {
            Toast.makeText(context, "Error: Expense ID not found.", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }

        setupObservers()

        // Corrected logic to check if fetching is needed:
        val currentResult = expenseViewModel.singleExpense.value
        val currentlyLoadedData = (currentResult as? ResultWrapper.Success<ExpenseResponse>)?.data

        if (currentlyLoadedData?.id != expenseId) {
            // Fetch if:
            // 1. Nothing is successfully loaded yet (currentlyLoadedData is null).
            // 2. Or, the successfully loaded data is for a different expenseId.
            // We also check if it's not already loading to prevent redundant calls.
            if (currentResult !is ResultWrapper.Loading) {
                expenseViewModel.fetchExpenseById(expenseId)
            }
            // If currentResult is ResultWrapper.Loading, we assume it's fetching (possibly the correct one).
            // The observer will handle the outcome.
        } else {
            // Data for the correct expenseId is already in a Success state and loaded.
            // The observer (setupObservers) will ensure populateDetails is called if the view is being recreated.
            // If `populateDetails` is not called automatically by the observer (e.g. config change, live data had value),
            // you might call it here, but typically the observer handles this.
            // populateDetails(currentlyLoadedData) // This would be redundant if observer works as expected.
        }


        binding.btnEditExpense.setOnClickListener {
            val action = ExpenseDetailFragmentDirections.actionExpenseDetailFragmentToEditExpenseFragment(expenseId)
            findNavController().navigate(action)
        }

        binding.btnDeleteExpense.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun setupObservers() {
        // Observer for fetching the single expense detail (this part looks mostly okay from your snippet)
        expenseViewModel.singleExpense.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultWrapper.Loading -> {
                    binding.progressBarDetail.isVisible = true
                    binding.layoutButtons.isVisible = false
                    // Hide content while loading details
                    binding.tvDetailTitle.visibility = View.INVISIBLE
                    binding.tvDetailAmount.visibility = View.INVISIBLE
                    binding.tvDetailCategory.visibility = View.INVISIBLE
                    binding.tvDetailDate.visibility = View.INVISIBLE
                    binding.tvDetailTitleLabel.visibility = View.INVISIBLE
                    binding.tvDetailAmountLabel.visibility = View.INVISIBLE
                    binding.tvDetailCategoryLabel.visibility = View.INVISIBLE
                    binding.tvDetailDateLabel.visibility = View.INVISIBLE
                }
                is ResultWrapper.Success -> {
                    // Only populate if the success data is for the current expenseId
                    if (result.data.id == expenseId) {
                        binding.progressBarDetail.isVisible = false
                        binding.layoutButtons.isVisible = true
                        // Make content visible
                        binding.tvDetailTitle.visibility = View.VISIBLE
                        binding.tvDetailAmount.visibility = View.VISIBLE
                        binding.tvDetailCategory.visibility = View.VISIBLE
                        binding.tvDetailDate.visibility = View.VISIBLE
                        binding.tvDetailTitleLabel.visibility = View.VISIBLE
                        binding.tvDetailAmountLabel.visibility = View.VISIBLE
                        binding.tvDetailCategoryLabel.visibility = View.VISIBLE
                        binding.tvDetailDateLabel.visibility = View.VISIBLE
                        populateDetails(result.data)
                    }
                }
                is ResultWrapper.Error -> {
                    binding.progressBarDetail.isVisible = false
                    binding.layoutButtons.isVisible = false // Consider if buttons should be enabled to retry
                    Toast.makeText(context, "Error fetching details: ${result.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Observer for the result of operations like delete (this needs correction)
        expenseViewModel.expenseOperationResult.observe(viewLifecycleOwner) { result -> // result is ResultWrapper<Boolean>?
            // Handle the null case first (when the event has been cleared)
            if (result == null) {
                // Event was cleared, ensure UI is in a neutral state for this operation
                if (binding.progressBarDetail.isVisible) { // If progress was for delete
                    binding.progressBarDetail.isVisible = false
                }
                binding.layoutButtons.isEnabled = true // Re-enable buttons if they were disabled
                return@observe
            }

            // If result is not null, proceed
            // Hide progress bar if it was visible for this operation and result is no longer Loading
            if (binding.progressBarDetail.isVisible && result !is ResultWrapper.Loading) {
                binding.progressBarDetail.isVisible = false
                binding.layoutButtons.isEnabled = true // Re-enable buttons
            }

            when (result) {
                is ResultWrapper.Loading -> {
                    // This state is typically set by the ViewModel when an operation starts.
                    // The progressBarDetail might have already been set true in showDeleteConfirmationDialog.
                    // binding.progressBarDetail.isVisible = true // Can be redundant if already set
                    binding.layoutButtons.isEnabled = false // Disable buttons during operation
                }
                is ResultWrapper.Success -> {
                    if (result.data) { // True for successful operation (e.g., delete)
                        Toast.makeText(context, "Expense deleted successfully.", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack() // Go back to list
                    }
                    expenseViewModel.clearExpenseOperationResult() // <-- ADD THIS to consume the event
                }
                is ResultWrapper.Error -> {
                    Toast.makeText(context, "Operation failed: ${result.message}", Toast.LENGTH_LONG).show()
                    expenseViewModel.clearExpenseOperationResult() // <-- ADD THIS to consume the event
                }
            }
        }
    }

    private fun populateDetails(expense: ExpenseResponse) {
        binding.tvDetailTitle.text = expense.title
        binding.tvDetailAmount.text = "${CurrencyFormatter.formatAmount(expense.amount, expense.currency)} ${expense.currency.uppercase()}"
        binding.tvDetailCategory.text = expense.category.toDisplayCase() // Using extension function

        val parsedDate = DateUtils.parseDate(expense.date, com.example.expensetracker.utils.Constants.API_DATE_FORMAT)
        binding.tvDetailDate.text = if (parsedDate != null) {
            DateUtils.formatDate(parsedDate, com.example.expensetracker.utils.Constants.DISPLAY_DATE_FORMAT)
        } else {
            expense.date
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.confirm_delete_title))
            .setMessage(getString(R.string.confirm_delete_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                if (!NetworkUtils.isNetworkAvailable(requireContext())) {
                    Toast.makeText(context, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                binding.progressBarDetail.isVisible = true // Show progress specifically for delete
                binding.layoutButtons.isEnabled = false // Disable buttons during delete
                expenseViewModel.deleteExpense(expenseId)
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // It's good practice to remove observers from shared ViewModels if they are tied to this
        // fragment's specific instance logic, but LiveData handles lifecycle awareness well.
        // If expenseOperationResult was ONLY for this fragment's delete, you might consider
        // resetting it in the ViewModel or using SingleLiveEvent pattern for it.
    }

    // Helper extension function (consider moving to a common StringUtils.kt file)
    private fun String.toDisplayCase(): String = this.replace("_", " ").split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }
    }
}