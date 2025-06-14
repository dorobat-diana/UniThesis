package triptag.app.feature.register.use_case

import android.util.Patterns
import triptag.app.feature.register.data.RegisterRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: RegisterRepository
) {

    suspend operator fun invoke(email: String, password: String): Result<String> {

        if (email.isBlank()) {
            return Result.failure(Exception("Email cannot be empty"))
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Invalid email format"))
        }

        if (password.isBlank()) {
            return Result.failure(Exception("Password cannot be empty"))
        }

        if (password.length < 8) {
            return Result.failure(Exception("Password must be at least 8 characters long"))
        }

        if (!password.any { it.isDigit() }) {
            return Result.failure(Exception("Password must contain at least one digit"))
        }

        if (!password.any { it.isUpperCase() }) {
            return Result.failure(Exception("Password must contain at least one uppercase letter"))
        }

        if (!password.any { it in "!@#$%^&*()-_=+[]{}|;:'\",.<>?/" }) {
            return Result.failure(Exception("Password must contain at least one special character"))
        }

        return repository.registerUser(email, password)
    }
}
