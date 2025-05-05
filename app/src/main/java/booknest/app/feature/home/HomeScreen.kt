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

@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel: HomeViewModel = hiltViewModel()

    val users by viewModel.users.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.searchUsers(it.text)  // Trigger search on change
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

        // Loading state
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = colorResource(id = R.color.sand_storm)
            )
        }

        // Error state
        error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        // Users list
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(users) { user ->
                UserItem(user = user) {
                    navController.navigate("other_users/${user.uid}")
                }
            }
        }
    }
}