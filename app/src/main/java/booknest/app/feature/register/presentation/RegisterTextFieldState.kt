package booknest.app.feature.register.presentation

data class RegisterTextFieldState(
    val text: String = "",
    val hint: String = "",
    val isHintVisible: Boolean = true
)