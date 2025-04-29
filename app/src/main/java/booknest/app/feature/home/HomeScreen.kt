package booknest.app.feature.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(uid: String?) {
    Text(text = "Welcome, $uid!")
}
