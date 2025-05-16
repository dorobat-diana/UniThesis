package booknest.app.feature.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.google.maps.android.compose.GoogleMap
@Composable
fun MapScreen() {
    GoogleMap(
        modifier = Modifier.fillMaxSize()
    )
}