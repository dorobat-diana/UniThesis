package booknest.app.feature.post

import android.Manifest
import android.content.Context
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import booknest.app.feature.post.presentation.PostViewModel
import com.google.android.gms.location.*
import booknest.app.R
import java.io.File

@Composable
fun PostScreen(
    viewModel: PostViewModel = hiltViewModel(),
    context: Context = LocalContext.current
) {

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationRequest = remember {
        LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
    }

    var permissionGranted by remember { mutableStateOf(false) }

    val attractions = viewModel.nearbyAttractions

    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && photoUri != null) {
            viewModel.createPost(photoUri!!)
        }
    }


    // Check if permission is already granted before requesting it
    LaunchedEffect(Unit) {
        val permissionStatus = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionStatus == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            permissionGranted = true
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Request location updates only when permission is granted
    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            try {
                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        val location: Location? = result.lastLocation
                        if (location != null) {
                            viewModel.loadNearbyAttractions(location.latitude, location.longitude)
                        }
                    }
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                Toast.makeText(context, "Location permission is missing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (permissionGranted) {
            // "Nearby Attractions" Text centered and calligraphic
            Text(
                text = "Nearby Attractions:",
                color = Color(ContextCompat.getColor(context, R.color.sand_storm)),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.SansSerif, // Apply calligraphic font
                modifier = Modifier
                    .fillMaxWidth()

            )

            Spacer(modifier = Modifier.padding(top = 16.dp))

            if (attractions.isEmpty()) {
                Text(
                    text = "No attractions found within 500 meters.",
                    color = Color(ContextCompat.getColor(context, R.color.sand_storm)),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.SansSerif
                )
            } else {
                attractions.forEach {
                    // Transparent background box for each attraction
                    Box(
                        modifier = Modifier
                            .background(Color(ContextCompat.getColor(context, R.color.selected))) // Transparent white background (adjust opacity)
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "${it.name}",
                            color = Color(ContextCompat.getColor(context, R.color.sand_storm)),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.SansSerif, // Apply calligraphic font
                            modifier = Modifier
                                .clickable {
                                    viewModel.onAttractionSelected(it.name)

                                    val photoFile = File.createTempFile("photo_", ".jpg", context.cacheDir).apply {
                                        createNewFile()
                                        deleteOnExit()
                                    }
                                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                                    photoUri = uri
                                    launcher.launch(uri)

                                }
                        )
                    }
                }
            }
        } else {
            Text(
                text = "Location permission is required to show nearby attractions.",
                color = Color(ContextCompat.getColor(context, R.color.sand_storm)),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive
            )
        }
    }
}
