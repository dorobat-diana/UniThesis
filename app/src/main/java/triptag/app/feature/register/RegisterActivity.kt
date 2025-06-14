package triptag.app.feature.register

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import triptag.app.MainActivity
import triptag.app.R
import triptag.app.feature.register.presentation.RegisterViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var confirmPassword: TextInputEditText
    private lateinit var registrationButton: Button
    private lateinit var progressBar: ProgressBar

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initializeViews()
        setupListeners()
        observeRegistrationState()
    }

    private fun initializeViews() {
        email = findViewById(R.id.editTextEmail)
        password = findViewById(R.id.editTextPassword)
        confirmPassword = findViewById(R.id.editTextConfirmPassword)
        registrationButton = findViewById(R.id.RegisterButton)
        progressBar = findViewById(R.id.progressBar)
    }


    private fun setupListeners() {
        email.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(RegisterEvent.EnteredEmail(text.toString()))
        }
        password.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(RegisterEvent.EnteredPassword(text.toString()))
        }
        confirmPassword.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(RegisterEvent.ConfirmedPassword(text.toString()))
        }

        registrationButton.setOnClickListener {
            viewModel.onEvent(RegisterEvent.SubmitRegistration)
        }

    }

    private fun observeRegistrationState() {
        lifecycleScope.launch {
            viewModel.registrationState.collect { state ->
                when (state) {
                    is RegisterState.Loading -> {
                        progressBar.visibility = ProgressBar.VISIBLE
                        registrationButton.isEnabled = false
                    }

                    is RegisterState.Success -> {
                        progressBar.visibility = ProgressBar.INVISIBLE
                        registrationButton.isEnabled = true
                        showSuccessMessage("Registration successful")
                        saveUserUID(state.uid)
                        navigateToMainActivity(state.uid)
                        finish()
                    }

                    is RegisterState.Error -> {
                        progressBar.visibility = ProgressBar.INVISIBLE
                        registrationButton.isEnabled = true
                        showErrorMessage(state.message)
                    }

                    else -> {
                        progressBar.visibility = ProgressBar.INVISIBLE
                    }
                }
            }
        }
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccessMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun saveUserUID(uid: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("USER_UID", uid)
        editor.apply()
    }

    private fun navigateToMainActivity(uid: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("USER_UID", uid)
        startActivity(intent)
    }
}
