package booknest.app.feature.friends

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import booknest.app.R
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import booknest.app.feature.friends.presentation.FriendsViewModel
import booknest.app.feature.profil.data.UserProfile
import android.util.Log

@Composable
fun FriendsListScreen(
    viewModel: FriendsViewModel = hiltViewModel(),
    navController: NavHostController,
    uid: String
) {
    val friends by viewModel.friends.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Log when the friends list is being loaded
    LaunchedEffect(uid) {
        Log.d("FriendsListScreen", "Loading friends for UID: $uid")
        viewModel.loadFriends(uid)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        IconButton(
            onClick = {
                Log.d("FriendsListScreen", "Back button clicked")
                navController.popBackStack()
            },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colorResource(id = R.color.sand_storm))
        }

        Text(
            text = "Friends",
            style = MaterialTheme.typography.headlineSmall.copy(fontStyle = FontStyle.Italic),
            color = colorResource(id = R.color.sand_storm),
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
        )

        when {
            loading -> {
                Log.d("FriendsListScreen", "Loading is true, showing progress bar")
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colorResource(id = R.color.sand_storm))
                }
            }
            error != null -> {
                Log.e("FriendsListScreen", "Error occurred: $error")
                Text(text = error ?: "", color = MaterialTheme.colorScheme.error)
            }
            else -> {
                Log.d("FriendsListScreen", "Friends loaded: ${friends.size}")
                LazyColumn {
                    items(friends) { friend ->
                        FriendListItem(friend = friend)
                    }
                }
            }
        }
    }
}


@Composable
fun FriendListItem(friend: UserProfile) {
    Log.d("FriendListItem", "Displaying friend: ${friend.username ?: "Unknown"}")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                Log.d("FriendListItem", "Friend clicked: ${friend.username ?: "Unknown"}")
            }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Log.d("FriendListItem", "Loading profile picture for: ${friend.profilePictureUrl}")
        Image(
            painter = rememberAsyncImagePainter(friend.profilePictureUrl),
            contentDescription = "Friend profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = friend.username ?: "Unknown",
                style = MaterialTheme.typography.bodyLarge,
                color = colorResource(id = R.color.sand_storm)
            )
            Text(
                text = friend.caption,
                style = MaterialTheme.typography.bodySmall,
                color = colorResource(id = R.color.sand_storm)
            )
        }
    }
}

