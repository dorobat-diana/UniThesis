package booknest.app.feature.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import booknest.app.feature.challanges.ChallengesScreen
import booknest.app.feature.map.MapScreen
import booknest.app.feature.post.PostScreen
import booknest.app.feature.profil.ProfileScreen
import booknest.app.feature.utils.BottomNavItem
import booknest.app.feature.utils.BottomNavigationBar

@Composable
fun MainScreen(uid: String?) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen(uid) }
            composable(BottomNavItem.Map.route) { MapScreen() }
            composable(BottomNavItem.Post.route) { PostScreen() }
            composable(BottomNavItem.Challenges.route) { ChallengesScreen() }
            composable(BottomNavItem.Profile.route) { ProfileScreen(uid) }
        }
    }
}
