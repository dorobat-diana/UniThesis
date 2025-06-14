package triptag.app.feature.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import triptag.app.feature.login.LogInEvent
import triptag.app.feature.login.LogInState
import triptag.app.feature.login.use_case.LogInUseCase
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val logInUseCase: LogInUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _loginState = MutableStateFlow<LogInState>(LogInState.Idle)

    val loginState: StateFlow<LogInState> = _loginState

    fun onEvent(event: LogInEvent) {
        when (event) {
            is LogInEvent.EnteredEmail -> {
                _email.value = event.email
            }

            is LogInEvent.EnteredPassword -> {
                _password.value = event.password
            }

            LogInEvent.Submit -> {
                logInUser()
            }
        }
    }

    private fun logInUser() {
        val emailValue = _email.value
        val passwordValue = _password.value

        if (emailValue.isBlank() || passwordValue.isBlank()) {
            _loginState.value = LogInState.Error("Email and password cannot be empty")
            return
        }

        viewModelScope.launch {
            _loginState.value = LogInState.Loading
            val result = logInUseCase(emailValue, passwordValue)
            _loginState.value = result.fold(
                onSuccess = { LogInState.Success(result.getOrNull() ?: "") },
                onFailure = { LogInState.Error(it.message ?: "Unknown error") }
            )
        }
    }
}
