package triptag.app.feature.login

sealed class LogInState {
    object Idle : LogInState()
    object Loading : LogInState()
    data class Success(val uid: String) : LogInState()
    data class Error(val message: String) : LogInState()
}
