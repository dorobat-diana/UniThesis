package booknest.app.feature.profil
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ProfileScreen(uid: String?) {
    Text("Profile - UID: ${uid ?: "Unknown"}")
}
