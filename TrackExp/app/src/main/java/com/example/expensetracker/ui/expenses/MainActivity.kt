package com.example.expensetracker.ui.expenses

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.expensetracker.R
import com.example.expensetracker.databinding.ActivityMainBinding
import com.example.expensetracker.ui.auth.LoginActivity
import com.example.expensetracker.viewmodel.AuthViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain)

        if (!authViewModel.isLoggedIn()) {
            navigateToLogin()
            return
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        // Define top-level destinations. The Up button will not be shown for these.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.expenseListFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fabAddExpense.setOnClickListener {
            // Ensure the current destination is ExpenseListFragment to avoid issues
            // or define a global action if FAB should navigate from other screens too.
            if (navController.currentDestination?.id == R.id.expenseListFragment) {
                navController.navigate(R.id.action_expenseListFragment_to_addExpenseFragment)
            }
        }

        // Listener to update FAB visibility based on destination (optional)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.expenseListFragment) {
                binding.fabAddExpense.show()
            } else {
                binding.fabAddExpense.hide()
            }
            // Update Toolbar title based on fragment label
            supportActionBar?.title = destination.label
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // Handles the Up button action in the ActionBar.
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // MainActivity handles global menu items like logout.
    // Fragment-specific menu items (search, filter types) are handled within the fragments themselves using MenuProvider.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                authViewModel.logout()
                navigateToLogin()
                true
            }
            // If you had other global menu items, handle them here.
            // For navigation items controlled by NavigationUI, onNavDestinationSelected can be used.
            // return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}