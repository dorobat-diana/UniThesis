package booknest.app.feature.login

sealed class LogInEvent {
    data class EnteredEmail(val email: String) : LogInEvent()
    data class EnteredPassword(val password: String) : LogInEvent()
    object Submit : LogInEvent()
}