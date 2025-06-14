package triptag.app.feature.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import triptag.app.MainActivity
import triptag.app.R
import triptag.app.feature.login.presentation.LogInViewModel
import triptag.app.feature.register.RegisterActivity

@AndroidEntryPoint
class LogInActivity : AppCompatActivity() {

    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var newAccountText: TextView
    private lateinit var progressBar: ProgressBar

    private val viewModel: LogInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        initializeViews()
        setupListeners()
        observeLoginState()
    }

    private fun initializeViews() {
        email = findViewById(R.id.editTextEmail)
        password = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.RegisterButton)
        newAccountText = findViewById(R.id.NewAccount)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        email.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(LogInEvent.EnteredEmail(text.toString()))
        }
        password.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(LogInEvent.EnteredPassword(text.toString()))
        }

        loginButton.setOnClickListener {
            viewModel.onEvent(LogInEvent.Submit)
        }

        newAccountText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observeLoginState() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is LogInState.Loading -> {
                        progressBar.visibility = ProgressBar.VISIBLE
                        loginButton.isEnabled = false
                    }

                    is LogInState.Success -> {
                        progressBar.visibility = ProgressBar.INVISIBLE
                        loginButton.isEnabled = true
                        showSuccessMessage("Login successful")
                        saveUserUID(state.uid)
                        navigateToMainActivity(state.uid)
                        finish()
                    }

                    is LogInState.Error -> {
                        progressBar.visibility = ProgressBar.INVISIBLE
                        loginButton.isEnabled = true
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
