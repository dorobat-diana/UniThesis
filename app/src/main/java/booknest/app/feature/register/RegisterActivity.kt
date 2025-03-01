package booknest.app.feature.register

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import booknest.app.R
import booknest.app.feature.login.LogInActivity
import booknest.app.feature.register.presentation.RegisterViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var registrationButton: Button
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        email = findViewById(R.id.EmailBox)
        password = findViewById(R.id.editTextTextPassword)
        confirmPassword = findViewById(R.id.editTextTextPassword2)
        registrationButton = findViewById(R.id.RegisterButton)
        loginButton = findViewById(R.id.LogInButton)
        progressBar = findViewById(R.id.progressBar)

        lifecycleScope.launch {
            viewModel.eventFlow.collectLatest { event ->
                when(event) {
                    is RegisterViewModel.UiEvent.ShowSnackBar -> TODO()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.email.collectLatest { emailState ->
                if (emailState.text.isEmpty()) {
                    email.hint = emailState.hint
                } else {
                    email.setText(emailState.text)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.password.collectLatest { passwordState ->
                if (passwordState.text.isEmpty()) {
                    password.hint = passwordState.hint
                } else {
                    password.setText(passwordState.text)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.confirmPassword.collectLatest { confirmPasswordState ->
                if (confirmPasswordState.text.isEmpty()) {
                    confirmPassword.hint = confirmPasswordState.hint
                } else {
                    confirmPassword.setText(confirmPasswordState.text)
                }
            }
        }

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
