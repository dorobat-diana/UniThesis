package booknest.app.feature.register.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import booknest.app.feature.register.RegisterEvent
import booknest.app.feature.register.RegisterState
import booknest.app.feature.register.use_case.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor (
    private val registerUseCase: RegisterUseCase
): ViewModel() {

    private val _email = MutableStateFlow(
        RegisterTextFieldState()
    )

    private val _password = MutableStateFlow(
        RegisterTextFieldState(
        )
    )

    private val _confirmPassword = MutableStateFlow(
        RegisterTextFieldState(
        )
    )
    private val _registrationState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registrationState: StateFlow<RegisterState> = _registrationState

    fun onEvent(event: RegisterEvent) {
        when(event) {
            is RegisterEvent.EnteredEmail -> {
                _email.value = _email.value.copy(
                    text = event.value
                )
            }
            is RegisterEvent.ConfirmedPassword -> {
                _confirmPassword.value = _confirmPassword.value.copy(
                    text = event.value
                )
            }
            is RegisterEvent.EnteredPassword -> {
                _password.value = _password.value.copy(
                    text = event.value
                )
            }
            RegisterEvent.SubmitRegistration -> {
                registerUser()
            }
        }
    }
    private fun registerUser() {
        val emailValue = _email.value.text
        val passwordValue = _password.value.text
        val confirmPasswordValue = _confirmPassword.value.text

        if (passwordValue != confirmPasswordValue) {
            _registrationState.value = RegisterState.Error("Passwords do not match")
            return
        }

        viewModelScope.launch {
            _registrationState.value = RegisterState.Loading
            val result = registerUseCase(emailValue, passwordValue)
            _registrationState.value = result.fold(
                onSuccess = { RegisterState.Success(result.getOrNull() ?: "") },
                onFailure = { RegisterState.Error(it.message ?: "Unknown error") }
            )
        }
    }

}
