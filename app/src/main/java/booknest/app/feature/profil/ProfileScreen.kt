package booknest.app.feature.profil

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import booknest.app.feature.profil.presentation.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import booknest.app.R
import booknest.app.feature.home.presentation.UserItem
import booknest.app.feature.post.presentation.PostItem
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlin.collections.get

@Composable
fun ProfileScreen(navController: NavHostController, uid: String?, context: Context = LocalContext.current) {
    val viewModel: ProfileViewModel = hiltViewModel()

    val currentUserUid = remember { FirebaseAuth.getInstance().currentUser?.uid }
    val isOwnProfile = currentUserUid == uid

    val user by viewModel.profile.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isFriend by viewModel.isFriend.collectAsState()
    val posts by viewModel.posts.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var editedUsername by remember { mutableStateOf("") }
    var editedCaption by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            viewModel.updateProfilePictureFromBitmap(it)
        }
    }


    LaunchedEffect(uid) {
        uid?.let {
            viewModel.fetchUserPosts(it)
            viewModel.loadUser(it)
            currentUserUid?.let { currentUid ->
                viewModel.checkIfFriend(currentUid, it)
            }
        }
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
                if (!isOwnProfile) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colorResource(id = R.color.sand_storm))
                    }
                }
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(CircleShape)
                        .clickable(enabled = isOwnProfile) {
                            if (isOwnProfile) {
                                launcher.launch(null)
                            }
                        }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(profile.profilePictureUrl),
                        contentDescription = "Profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                    if (isOwnProfile) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x55000000), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Edit",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))

                if (isEditing) {
                    OutlinedTextField(
                        value = editedUsername,
                        onValueChange = { editedUsername = it },
                        label = { Text("Username") },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = colorResource(
                                id = R.color.sand_storm
                            )
                        ),
                        colors =  OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colorResource(id = R.color.sand_storm),
                            unfocusedTextColor = colorResource(id = R.color.sand_storm),
                            cursorColor = colorResource(id = R.color.sand_storm),
                            focusedLabelColor = colorResource(id = R.color.sand_storm),
                            unfocusedLabelColor = colorResource(id = R.color.sand_storm),
                            focusedBorderColor = colorResource(id = R.color.sand_storm),
                            unfocusedBorderColor = colorResource(id = R.color.sand_storm)
                        ),
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
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = colorResource(
                                id = R.color.sand_storm
                            )
                        ),
                        colors =  OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colorResource(id = R.color.sand_storm),
                            unfocusedTextColor = colorResource(id = R.color.sand_storm),
                            cursorColor = colorResource(id = R.color.sand_storm),
                            focusedLabelColor = colorResource(id = R.color.sand_storm),
                            unfocusedLabelColor = colorResource(id = R.color.sand_storm),
                            focusedBorderColor = colorResource(id = R.color.sand_storm),
                            unfocusedBorderColor = colorResource(id = R.color.sand_storm)
                        ),
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
                        color = colorResource(id = R.color.sand_storm),
                        modifier = Modifier.clickable {
                            navController.navigate("friends_screen/${profile.uid}")
                        }
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

                if (!isOwnProfile && isFriend != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                currentUserUid?.let { currentUid ->
                                    if (isFriend == true) {
                                        viewModel.removeFriend(currentUid, uid!!)
                                    } else {
                                        viewModel.addFriend(currentUid, uid!!)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.citric)
                            )
                        ) {
                            Text(
                                text = if (isFriend == true) "Unfriend" else "Add Friend",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.White,
                                    fontStyle = FontStyle.Italic
                                )
                            )
                        }
                    }
                }

                Divider(
                    color = colorResource(id = R.color.sand_storm),
                    thickness = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    if (posts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.Center)
                            ) {
                                Text(
                                    text = "No posts yet",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontStyle = FontStyle.Italic),
                                    color = colorResource(id = R.color.sand_storm)
                                )
                            }
                        }
                    } else {

                        items(posts) { post ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = colorResource(id = R.color.citric)
                                ),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Box(modifier = Modifier.padding(12.dp)) {
                                    PostItem(post = post, userName = user!!.username.toString())
                                }
                            }
                        }
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
