package com.example.expensetracker.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.expensetracker.api.models.request.RegisterRequest
import com.example.expensetracker.databinding.ActivityRegisterBinding // Generated ViewBinding class
import com.example.expensetracker.utils.NetworkUtils
import com.example.expensetracker.utils.ResultWrapper
import com.example.expensetracker.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()

        binding.btnRegister.setOnClickListener {
            handleRegister()
        }

        binding.tvGoToLogin.setOnClickListener {
            // Navigate to LoginActivity, potentially clearing this one from stack
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun handleRegister() {
        val username = binding.etUsernameRegister.text.toString().trim()
        val email = binding.etEmailRegister.text.toString().trim()
        val password = binding.etPasswordRegister.text.toString().trim()

        var isValid = true
        if (username.isEmpty()) {
            binding.tilUsernameRegister.error = getString(com.example.expensetracker.R.string.required_field)
            isValid = false
        } else {
            binding.tilUsernameRegister.error = null
        }

        if (email.isEmpty()) {
            binding.tilEmailRegister.error = getString(com.example.expensetracker.R.string.required_field)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmailRegister.error = "Invalid email format"
            isValid = false
        } else {
            binding.tilEmailRegister.error = null
        }

        if (password.isEmpty()) {
            binding.tilPasswordRegister.error = getString(com.example.expensetracker.R.string.required_field)
            isValid = false
        } else if (password.length < 6) {
            binding.tilPasswordRegister.error = "Password must be at least 6 characters"
            isValid = false
        }
        else {
            binding.tilPasswordRegister.error = null
        }

        if (!isValid) return

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, getString(com.example.expensetracker.R.string.error_network), Toast.LENGTH_SHORT).show()
            return
        }

        authViewModel.register(RegisterRequest(username, email, password))
    }

    private fun setupObservers() {
        authViewModel.registerResult.observe(this) { result ->
            when (result) {
                is ResultWrapper.Loading -> {
                    binding.progressBarRegister.isVisible = true
                    binding.btnRegister.isEnabled = false
                }
                is ResultWrapper.Success -> {
                    binding.progressBarRegister.isVisible = false
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(this, result.data.message, Toast.LENGTH_LONG).show()
                    // Navigate to LoginActivity
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                }
                is ResultWrapper.Error -> {
                    binding.progressBarRegister.isVisible = false
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(this, "Registration Failed: ${result.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}