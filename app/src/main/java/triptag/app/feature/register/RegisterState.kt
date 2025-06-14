package triptag.app.feature.register

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val uid: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}
