package booknest.app.feature.profil
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import booknest.app.feature.profil.presentation.ProfileViewModel

@Composable
fun ProfileScreen(uid: String?) {
    val viewModel: ProfileViewModel = hiltViewModel()

    LaunchedEffect(uid) {
        Log.d("ProfileScreen", "LaunchedEffect triggered with UID: $uid")
        uid?.let { viewModel.loadUser(it) }
    }

    val user by viewModel.profile.collectAsState()

    Log.d("ProfileScreen", "Current user state: $user")

    user?.let { profile ->
        Log.d("ProfileScreen", "Rendering profile for: ${profile.username}")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(profile.profilePictureUrl),
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = profile.username ?: "",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = profile.caption.ifEmpty { "No caption yet" },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text("Friends: ${profile.friendsCount}")
                Text("Posts: ${profile.postsCount}")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Posts will be shown here...",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    } ?: Text("Loading profile...", modifier = Modifier.padding(16.dp))
}
