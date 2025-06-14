package triptag.app.feature.register

sealed class RegisterEvent {
    data class EnteredEmail(val value: String) : RegisterEvent()

    data class EnteredPassword(val value: String) : RegisterEvent()

    data class ConfirmedPassword(val value: String) : RegisterEvent()

    object SubmitRegistration : RegisterEvent()
}
