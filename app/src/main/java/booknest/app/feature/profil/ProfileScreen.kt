package booknest.app.feature.profil

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontStyle
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
            // Profile picture
            Image(
                painter = rememberAsyncImagePainter(profile.profilePictureUrl),
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Username Text
            Text(
                text = profile.username ?: "No username",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontStyle = FontStyle.Italic
                ),
                color = Color(0xFFFBE8DA), // Sand Storm color
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Caption Text
            Text(
                text = profile.caption.ifEmpty { "No caption yet" },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic
                ),
                color = Color(0xFFFBE8DA), // Sand Storm color
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Friends and Posts count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = "Friends: ${profile.friendsCount}", style = MaterialTheme.typography.headlineSmall.copy(
                    fontStyle = FontStyle.Italic
                ),
                    color = Color(0xFFFBE8DA))
                Text(text = "Posts: ${profile.postsCount}", style = MaterialTheme.typography.headlineSmall.copy(
                    fontStyle = FontStyle.Italic
                ),
                    color = Color(0xFFFBE8DA))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Posts Grid (Placeholder for now)
            Text(
                text = "Posts will be shown here...",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Placeholder for Posts Grid
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Post Grid Layout",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    } ?: Text("Loading profile...", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall.copy(
        fontStyle = FontStyle.Italic
    ),
        color = Color(0xFFFBE8DA))
}
