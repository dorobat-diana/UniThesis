package booknest.app.feature.register.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import booknest.app.feature.register.RegisterEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor (): ViewModel() {

    private val _email = MutableStateFlow(
        RegisterTextFieldState(
            hint = "Enter Email"
        )
    )
    val email: StateFlow<RegisterTextFieldState> = _email.asStateFlow()

    private val _password = MutableStateFlow(
        RegisterTextFieldState(
            hint = "Enter Password"
        )
    )
    val password: StateFlow<RegisterTextFieldState> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow(
        RegisterTextFieldState(
            hint = "Confirm Password"
        )
    )
    val confirmPassword: StateFlow<RegisterTextFieldState> = _confirmPassword.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: RegisterEvent) {
        when(event) {
            is RegisterEvent.EnteredEmail -> {
                _email.value = _email.value.copy(
                    text = event.value
                )
            }
            is RegisterEvent.ChangeConfirmPasswordFocus -> {
                _confirmPassword.value = _confirmPassword.value.copy(
                    isHintVisible = !event.focusState.isFocused
                )
            }
            is RegisterEvent.ChangeEmailFocus ->  {
                _email.value = _email.value.copy(
                    isHintVisible = !event.focusState.isFocused
                )
            }
            is RegisterEvent.ChangePasswordFocus ->  {
                _password.value = _password.value.copy(
                    isHintVisible = !event.focusState.isFocused
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

    sealed class UiEvent {
        data class ShowSnackBar(val message: String): UiEvent()
    }
}
