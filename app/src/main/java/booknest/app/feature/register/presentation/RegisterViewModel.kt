package booknest.app.feature.register.presentation

import androidx.lifecycle.ViewModel
import booknest.app.feature.register.RegisterEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor (): ViewModel() {

    private val _email = MutableStateFlow(
        RegisterTextFieldState()
    )
    val email: StateFlow<RegisterTextFieldState> = _email.asStateFlow()

    private val _password = MutableStateFlow(
        RegisterTextFieldState(
        )
    )
    val password: StateFlow<RegisterTextFieldState> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow(
        RegisterTextFieldState(
        )
    )
    val confirmPassword: StateFlow<RegisterTextFieldState> = _confirmPassword.asStateFlow()

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
                // Handle registration
            }
        }
    }

}
