package booknest.app.feature.register

import androidx.compose.ui.focus.FocusState

sealed class RegisterEvent {
    data class EnteredEmail(val value: String) : RegisterEvent()
    data class ChangeEmailFocus(val focusState: FocusState) : RegisterEvent()

    data class EnteredPassword(val value: String) : RegisterEvent()
    data class ChangePasswordFocus(val focusState: FocusState) : RegisterEvent()

    data class ConfirmedPassword(val value: String) : RegisterEvent()
    data class ChangeConfirmPasswordFocus(val focusState: FocusState) : RegisterEvent()

    object SubmitRegistration : RegisterEvent()
}
