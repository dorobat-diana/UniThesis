package triptag.app.feature.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import triptag.app.feature.map.viewModel.MapViewModel


@Composable
fun MapScreen(userId: String, viewModel: MapViewModel = hiltViewModel()) {
    val attractions by viewModel.attractions.collectAsState()
    val selectedAttraction by viewModel.selectedAttraction.collectAsState()

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(Unit) {
        viewModel.loadVisitedAttractions(userId)
    }

    LaunchedEffect(selectedAttraction) {
        selectedAttraction?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    LatLng(it.coordinates.latitude, it.coordinates.longitude),
                    15f
                )
            )
        }
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            attractions.forEach { attraction ->
                Marker(
                    state = MarkerState(
                        position = LatLng(
                            attraction.coordinates.latitude,
                            attraction.coordinates.longitude
                        )
                    ),
                    title = attraction.name
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.8f))
                .clip(RoundedCornerShape(8.dp))
        ) {
            Text(
                text = "Your Visited Attractions",
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.Black
            )
            attractions.forEach { attraction ->
                Text(
                    text = attraction.name,
                    modifier = Modifier
                        .clickable { viewModel.selectAttraction(attraction) }
                        .padding(8.dp)
                )
            }
        }
    }
}
