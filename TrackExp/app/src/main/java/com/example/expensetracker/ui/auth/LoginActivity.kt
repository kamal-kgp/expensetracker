package com.example.expensetracker.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.expensetracker.api.models.request.LoginRequest
import com.example.expensetracker.databinding.ActivityLoginBinding // Generated ViewBinding class
import com.example.expensetracker.ui.expenses.MainActivity
import com.example.expensetracker.utils.NetworkUtils
import com.example.expensetracker.utils.ResultWrapper
import com.example.expensetracker.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // If already logged in, go to MainActivity
        if (authViewModel.isLoggedIn()) {
            navigateToMain()
            return
        }

        setupObservers()

        binding.btnLogin.setOnClickListener {
            handleLogin()
        }

        binding.tvGoToSignup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            // finish() // Optional: finish LoginActivity if you don't want it in back stack
        }
    }

    private fun handleLogin() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        var isValid = true
        if (username.isEmpty()) {
            binding.tilUsername.error = getString(com.example.expensetracker.R.string.required_field)
            isValid = false
        } else {
            binding.tilUsername.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(com.example.expensetracker.R.string.required_field)
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        if (!isValid) return

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, getString(com.example.expensetracker.R.string.error_network), Toast.LENGTH_SHORT).show()
            return
        }

        authViewModel.login(LoginRequest(username, password))
    }

    private fun setupObservers() {
        authViewModel.loginResult.observe(this) { result ->
            when (result) {
                is ResultWrapper.Loading -> {
                    binding.progressBarLogin.isVisible = true
                    binding.btnLogin.isEnabled = false
                }
                is ResultWrapper.Success -> {
                    binding.progressBarLogin.isVisible = false
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is ResultWrapper.Error -> {
                    binding.progressBarLogin.isVisible = false
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(this, "Login Failed: ${result.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}