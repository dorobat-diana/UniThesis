package booknest.app.feature.register

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import booknest.app.R
import booknest.app.feature.login.LogInActivity
import booknest.app.feature.register.presentation.RegisterViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var confirmPassword: TextInputEditText
    private lateinit var registrationButton: Button
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        email = findViewById(R.id.editTextEmail)
        password = findViewById(R.id.editTextPassword)
        confirmPassword = findViewById(R.id.editTextConfirmPassword)
        registrationButton = findViewById(R.id.RegisterButton)
        loginButton = findViewById(R.id.LogInButton)
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

        loginButton.setOnClickListener { navigateToLoginScreen() }
    }


    private fun navigateToLoginScreen() {
        startActivity(Intent(this, LogInActivity::class.java))
        finish()
    }
}
