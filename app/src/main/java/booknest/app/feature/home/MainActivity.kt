package booknest.app.feature.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the UID from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val uid = sharedPreferences.getString("USER_UID", null)

        setContent {
            MaterialTheme {
                Surface {
                    MainScreen(uid) // You can now call Composables here
                }
            }
        }
    }
}
