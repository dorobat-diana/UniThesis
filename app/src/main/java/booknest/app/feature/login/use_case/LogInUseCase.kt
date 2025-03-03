package booknest.app.feature.login.use_case

import android.util.Patterns
import booknest.app.feature.login.data.LogInRepository
import javax.inject.Inject

class LogInUseCase @Inject constructor(
    private val repository: LogInRepository
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

        return repository.login(email, password)
    }
}
