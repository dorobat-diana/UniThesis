package booknest.app.feature.profil

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontStyle
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import booknest.app.feature.profil.presentation.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import booknest.app.R

@Composable
fun ProfileScreen(uid: String?) {
    val viewModel: ProfileViewModel = hiltViewModel()

    val currentUserUid = remember { FirebaseAuth.getInstance().currentUser?.uid }
    val isOwnProfile = currentUserUid == uid

    val user by viewModel.profile.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var editedUsername by remember { mutableStateOf("") }
    var editedCaption by remember { mutableStateOf("") }

    LaunchedEffect(uid) {
        uid?.let { viewModel.loadUser(it) }
    }

    if (user != null) {
        val profile = user!!

        LaunchedEffect(profile) {
            editedUsername = profile.username ?: ""
            editedCaption = profile.caption
        }

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
                    .size(120.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = editedUsername,
                    onValueChange = { editedUsername = it },
                    label = { Text("Username") },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = colorResource(id = R.color.sand_storm)),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = profile.username ?: "No username",
                    style = MaterialTheme.typography.headlineSmall.copy(fontStyle = FontStyle.Italic),
                    color = colorResource(id = R.color.sand_storm),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            if (isEditing) {
                OutlinedTextField(
                    value = editedCaption,
                    onValueChange = { editedCaption = it },
                    label = { Text("Caption") },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = colorResource(id = R.color.sand_storm)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            } else {
                Text(
                    text = profile.caption.ifEmpty { "No caption yet" },
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    color = colorResource(id = R.color.sand_storm),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Friends: ${profile.friendsCount}",
                    style = MaterialTheme.typography.headlineSmall.copy(fontStyle = FontStyle.Italic),
                    color = colorResource(id = R.color.sand_storm)
                )
                Text(
                    text = "Posts: ${profile.postsCount}",
                    style = MaterialTheme.typography.headlineSmall.copy(fontStyle = FontStyle.Italic),
                    color = colorResource(id = R.color.sand_storm)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isOwnProfile) {
                Button(
                    onClick = {
                        if (isEditing) {
                            viewModel.updateProfile(editedUsername, editedCaption)
                        }
                        isEditing = !isEditing
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.citric),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(if (isEditing) "Save" else "Edit Profile")
                }
            }

            if (loading) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.sand_storm),
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
                )
            }

            if (error != null) {
                Text(
                    text = error ?: "",
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = colorResource(id = R.color.sand_storm),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}


