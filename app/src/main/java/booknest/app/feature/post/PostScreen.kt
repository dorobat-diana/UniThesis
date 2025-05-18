package booknest.app.feature.post

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import booknest.app.feature.post.presentation.AttractionViewModel
import com.google.android.gms.location.*
import booknest.app.R
import java.io.File

@Composable
fun PostScreen(
    userId: String,
    viewModel: AttractionViewModel = hiltViewModel(),
    context: Context = LocalContext.current
) {
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0L)
        .setMinUpdateDistanceMeters(10f)
        .setWaitForAccurateLocation(true)
        .build()


    var permissionGranted by remember { mutableStateOf(false) }

    val attractions = viewModel.nearbyAttractions

    val isLoading by viewModel.isLoading.collectAsState()

    val isCreatingPost by viewModel.isCreatingPost.collectAsState()

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
            viewModel.createPost(userId,photoUri!!, context)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    val postCreationStatus by viewModel.postCreationStatus.collectAsState()

    LaunchedEffect(postCreationStatus) {
        postCreationStatus?.let { result ->
            val message = if (result.isSuccess) {
                "Post created successfully"
            } else {
                "Post creation failed, the image did not pass the photo validation"
            }
            Log.d("CreatePost", "Snackbar message: $message")
            snackbarHostState.showSnackbar(message)
            viewModel.clearStatus()
        }
    }

    LaunchedEffect(Unit) {
        val permissionStatus = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            permissionGranted = true
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

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
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                val isError = snackbarData.visuals.message.contains("failed", ignoreCase = true)
                Snackbar(
                    containerColor = if (isError) Color.Red else Color.Green,
                    contentColor = Color.White
                ) {
                    Text(
                        text = snackbarData.visuals.message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)
            .padding(16.dp)) {
            if (permissionGranted) {
                Text(
                    text = "Nearby Attractions:",
                    color = Color(ContextCompat.getColor(context, R.color.sand_storm)),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontStyle = FontStyle.Italic
                    )
                )

                Spacer(modifier = Modifier.padding(top = 16.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(ContextCompat.getColor(context, R.color.sand_storm))
                        )
                    }
                } else {
                if (attractions.isEmpty()) {
                    Text(
                        text = "No attractions found within 500 meters.",
                        color = Color(ContextCompat.getColor(context, R.color.sand_storm)),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.SansSerif,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontStyle = FontStyle.Italic
                        )
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        attractions.forEach { attraction ->
                            Text(
                                text = attraction.name,
                                color = Color(ContextCompat.getColor(context, R.color.sand_storm)),
                                fontSize = 20.sp,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.White,
                                    fontStyle = FontStyle.Italic
                                ),
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.SansSerif,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.onAttractionSelected(attraction.name)

                                        val photoFile =
                                            File.createTempFile("photo_", ".jpg", context.cacheDir)
                                                .apply {
                                                    createNewFile()
                                                    deleteOnExit()
                                                }
                                        val uri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            photoFile
                                        )
                                        photoUri = uri
                                        launcher.launch(uri)
                                    }
                                    .background(
                                        Color(
                                            ContextCompat.getColor(
                                                context,
                                                R.color.citric
                                            )
                                        )
                                    )
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
            } else {
                Text(
                    text = "Location permission is required to show nearby attractions.",
                    color = Color(ContextCompat.getColor(context, R.color.sand_storm)),
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontStyle = FontStyle.Italic
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        if (isCreatingPost) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "AI validation...",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}
