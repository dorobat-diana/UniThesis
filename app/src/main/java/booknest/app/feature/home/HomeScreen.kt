package booknest.app.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import booknest.app.R
import booknest.app.feature.home.presentation.HomeViewModel
import booknest.app.feature.home.presentation.UserItem
import booknest.app.feature.post.presentation.PostItem
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.text.style.TextAlign
import booknest.app.feature.post.presentation.PostItemViewModel

@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel: HomeViewModel = hiltViewModel()
    val postViewModel: PostItemViewModel = hiltViewModel()

    val users by viewModel.users.collectAsState()
    val posts by postViewModel.posts.collectAsState()
    val userMap by viewModel.userMap.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    LaunchedEffect(userId) {
        userId?.let {
            viewModel.loadFriendMap(it)
            postViewModel.fetchFriendsPosts(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.searchUsers(it.text)
            },
            label = { Text("Search Users", color = colorResource(id = R.color.sand_storm)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colorResource(id = R.color.sand_storm),
                unfocusedTextColor = colorResource(id = R.color.sand_storm),
                cursorColor = colorResource(id = R.color.sand_storm),
                focusedLabelColor = colorResource(id = R.color.sand_storm),
                unfocusedLabelColor = colorResource(id = R.color.sand_storm),
                focusedBorderColor = colorResource(id = R.color.sand_storm),
                unfocusedBorderColor = colorResource(id = R.color.sand_storm)
            )
        )

        if (users.isNotEmpty()) {
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = {
                searchQuery = TextFieldValue("")
                viewModel.clearSearchResults()
            }) {
                Text("Cancel", color = colorResource(id = R.color.sand_storm))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = colorResource(id = R.color.sand_storm)
            )
        }

        error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        LazyColumn(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)

        ) {
            if (searchQuery.text.isNotBlank() && users.isNotEmpty()) {
                items(users) { user ->
                    UserItem(user = user) {
                        navController.navigate("other_users/${user.uid}")
                    }
                }
            } else {
                items(posts) { postUiState ->
                    val friendName = userMap[postUiState.post.userId]?.username ?: "Unknown"
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
                            PostItem(
                                post = postUiState.post,
                                userName = friendName,
                                onLikeClick = {
                                    postViewModel.toggleLike(userId.toString(), postUiState.post)
                                },
                                isLiked = postUiState.isLiked,
                                likeCount = postUiState.likeCount
                            )
                        }
                    }
                }


                item {
                    Text(
                        text = "No more posts",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        color = colorResource(id = R.color.sand_storm),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
