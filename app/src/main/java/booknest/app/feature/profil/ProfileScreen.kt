package booknest.app.feature.profil

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import booknest.app.feature.profil.presentation.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import booknest.app.R
import booknest.app.feature.post.presentation.PostItem
import booknest.app.feature.post.presentation.PostItemViewModel
import booknest.app.feature.profil.presentation.ProfileHeaderInfo


@Composable
fun ProfileScreen(navController: NavHostController, uid: String?, context: Context = LocalContext.current) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val postViewModel: PostItemViewModel = hiltViewModel()

    val currentUserUid = remember { FirebaseAuth.getInstance().currentUser?.uid }
    val isOwnProfile = currentUserUid == uid

    val user by viewModel.profile.collectAsState()
    val loadingPosts by postViewModel.loading.collectAsState()
    val loadingProfile by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isFriend by viewModel.isFriend.collectAsState()
    val posts by postViewModel.posts.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var editedUsername by remember { mutableStateOf("") }
    var editedCaption by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            viewModel.updateProfilePictureFromBitmap(it)
        }
    }

    // Pre-fetch colors to pass them down, avoiding repeated calls to colorResource
    val sandStormColor = colorResource(id = R.color.sand_storm)
    val citricColor = colorResource(id = R.color.citric)

    LaunchedEffect(uid) {
        uid?.let {
            postViewModel.fetchUserPosts(it)
            viewModel.loadUser(it)
            currentUserUid?.let { currentUid ->
                viewModel.checkIfFriend(currentUid, it)
            }
        }
    }

    if (loadingProfile && user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = sandStormColor)
        }
    } else if (user != null) {
        val profile = user!!

        LaunchedEffect(profile, isOwnProfile) { // Re-initialize if profile changes or it becomes own profile editing
            if (isOwnProfile) {
                editedUsername = profile.username ?: ""
                editedCaption = profile.caption ?: ""
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp), // Horizontal padding will be handled by items if needed or can be added here
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back Button (if not own profile)
            if (!isOwnProfile) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) { // Added horizontal padding to align with content
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = sandStormColor)
                        }
                    }
                }
            }

            // New Profile Header Item
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) { // Apply horizontal padding to this section
                    ProfileHeaderInfo(
                        profile = profile,
                        isOwnProfile = isOwnProfile,
                        isEditing = isEditing,
                        onEditToggle = { isEditing = !isEditing },
                        editedUsername = editedUsername,
                        onUsernameChange = { editedUsername = it },
                        editedCaption = editedCaption,
                        onCaptionChange = { editedCaption = it },
                        onUpdateProfile = { viewModel.updateProfile(editedUsername, editedCaption) },
                        onProfilePicChangeLaunch = { launcher.launch(null) },
                        isFriend = isFriend,
                        currentUserUid = currentUserUid,
                        targetUid = uid,
                        onAddFriend = { curUid, tarUid -> viewModel.addFriend(curUid, tarUid) },
                        onRemoveFriend = { curUid, tarUid -> viewModel.removeFriend(curUid, tarUid) },
                        navController = navController,
                        sandStormColor = sandStormColor,
                        citricColor = citricColor
                    )
                }
            }

            // Spacer and Divider
            item {
                Spacer(modifier = Modifier.height(24.dp)) // Increased space before divider
                Divider(
                    color = sandStormColor.copy(alpha = 0.5f), // Make divider a bit subtle
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp) // Padding for divider
                )
                Spacer(modifier = Modifier.height(16.dp)) // Space after divider, before posts
            }

            // Posts Section
            if (loadingPosts && posts.isEmpty()) {
                item {
                    CircularProgressIndicator(color = sandStormColor, modifier = Modifier.padding(16.dp))
                }
            } else if (posts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No posts yet", style = MaterialTheme.typography.headlineSmall.copy(fontStyle = FontStyle.Italic), color = sandStormColor)
                    }
                }
            } else {
                items(posts, key = { it.post.uid ?: java.util.UUID.randomUUID().toString() }) { postUiState ->
                    val userName = profile.username ?: "Unknown User" // Posts still need username for PostItem
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 12.dp)
                            .shadow(
                                elevation = 20.dp, // Higher elevation = deeper shadow
                                shape = RoundedCornerShape(16.dp), // More curve = softer shadow edge
                                ambientColor = Color.Black.copy(alpha = 0.6f), // Darker ambient shadow
                                spotColor = Color.Black.copy(alpha = 0.6f) // Darker directional shadow
                            ),
                        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.burn_red)),
                        elevation = CardDefaults.cardElevation(0.dp) // Remove default elevation to avoid doubling
                    ) {
                        PostItem( // Assuming PostItem is defined elsewhere as per previous steps
                            post = postUiState.post,
                            userName = userName,
                            onLikeClick = { currentUserUid?.let { likerId -> postViewModel.toggleLike(likerId, postUiState.post) } },
                            isLiked = postUiState.isLiked,
                            likeCount = postUiState.likeCount
                        )
                    }
                }
            }

            // Loading indicator for more posts
            if (loadingPosts && posts.isNotEmpty()) {
                item {
                    CircularProgressIndicator(color = sandStormColor, modifier = Modifier.padding(16.dp))
                }
            }

            // Error message
            error?.let {
                item {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    } else if (error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = sandStormColor)
        }
    }
}
