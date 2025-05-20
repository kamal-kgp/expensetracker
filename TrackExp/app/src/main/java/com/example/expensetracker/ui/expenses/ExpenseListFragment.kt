package com.example.expensetracker.ui.expenses

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker.R
import com.example.expensetracker.api.models.response.ExpenseResponse
import com.example.expensetracker.databinding.FragmentExpenseListBinding
import com.example.expensetracker.ui.expenses.adapter.ExpenseAdapter
import com.example.expensetracker.utils.NetworkUtils
import com.example.expensetracker.utils.ResultWrapper
import com.example.expensetracker.viewmodel.ExpenseViewModel

class ExpenseListFragment : Fragment() {

    private var _binding: FragmentExpenseListBinding? = null
    private val binding get() = _binding!!

    private val expenseViewModel: ExpenseViewModel by activityViewModels()
    private lateinit var expenseAdapter: ExpenseAdapter
    private var currentExpenseList: List<ExpenseResponse> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupMenu()

        // Fetch expenses if the list is currently empty or was an error state
        if (expenseViewModel.expenseList.value == null || expenseViewModel.expenseList.value !is ResultWrapper.Success) {
            fetchExpenses()
        }
    }

    private fun fetchExpenses() {
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            expenseViewModel.fetchAllExpenses()
        } else {
            binding.progressBarExpenses.isVisible = false
            Toast.makeText(context, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter { expense ->
            // Navigate to ExpenseDetailFragment using Safe Args
            val action = ExpenseListFragmentDirections.actionExpenseListFragmentToExpenseDetailFragment(expense.id)
            findNavController().navigate(action)
        }
        binding.rvExpenses.apply {
            adapter = expenseAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        expenseViewModel.expenseList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultWrapper.Loading -> {
                    binding.progressBarExpenses.isVisible = true
                    binding.tvNoExpenses.isVisible = false
                    binding.rvExpenses.isVisible = false
                }
                is ResultWrapper.Success -> {
                    binding.progressBarExpenses.isVisible = false
                    currentExpenseList = result.data
                    if (currentExpenseList.isEmpty()) {
                        binding.tvNoExpenses.isVisible = true
                        binding.rvExpenses.isVisible = false
                    } else {
                        binding.tvNoExpenses.isVisible = false
                        binding.rvExpenses.isVisible = true
                        expenseAdapter.submitList(currentExpenseList.toList()) // Ensure a new list is submitted
                    }
                }
                is ResultWrapper.Error -> {
                    binding.progressBarExpenses.isVisible = false
                    binding.tvNoExpenses.text = getString(R.string.error_fetching_expenses, result.message)
                    binding.tvNoExpenses.isVisible = true
                    binding.rvExpenses.isVisible = false
                    // Toast.makeText(context, getString(R.string.error_fetching_expenses, result.message), Toast.LENGTH_LONG).show()
                }
            }
        }
        expenseViewModel.expenseOperationResult.observe(viewLifecycleOwner) { result ->
            if ((result is ResultWrapper.Success && result.data) || result == null) {
                fetchExpenses() // Refresh list after add, update, delete
            } else if (result is ResultWrapper.Error) {
                Toast.makeText(context, "Operation failed: ${result.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchView.clearFocus()
                        return true
                    }
                    override fun onQueryTextChange(newText: String?): Boolean {
                        filterListByTitle(newText)
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_filter_today -> {
                        expenseViewModel.fetchTodaysExpenses()
                        true
                    }
                    R.id.action_filter_week -> {
                        expenseViewModel.fetchThisWeeksExpenses()
                        true
                    }
                    R.id.action_filter_month -> {
                        expenseViewModel.fetchThisMonthsExpenses()
                        true
                    }
                    R.id.action_custom_filter -> {
                        findNavController().navigate(ExpenseListFragmentDirections.actionExpenseListFragmentToFilterFragment())
                        true
                    }
                    R.id.action_view_summary -> {
                        findNavController().navigate(ExpenseListFragmentDirections.actionExpenseListFragmentToSummaryFragment())
                        true
                    }
                    // MainActivity handles R.id.action_logout
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun filterListByTitle(query: String?) {
        if (query.isNullOrEmpty()) {
            expenseAdapter.submitList(currentExpenseList.toList())
            binding.tvNoExpenses.isVisible = currentExpenseList.isEmpty()
            binding.rvExpenses.isVisible = currentExpenseList.isNotEmpty()
        } else {
            val filteredList = currentExpenseList.filter {
                it.title.contains(query, ignoreCase = true)
            }
            expenseAdapter.submitList(filteredList)
            binding.tvNoExpenses.isVisible = filteredList.isEmpty()
            binding.rvExpenses.isVisible = filteredList.isNotEmpty()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}