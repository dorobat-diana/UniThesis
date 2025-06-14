package triptag.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import triptag.app.feature.challanges.ChallengesScreen
import triptag.app.feature.friends.FriendsListScreen
import triptag.app.feature.home.HomeScreen
import triptag.app.feature.map.MapScreen
import triptag.app.feature.post.PostScreen
import triptag.app.feature.profil.ProfileScreen
import triptag.app.feature.utils.BottomNavItem
import triptag.app.feature.utils.BottomNavigationBar

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
            composable(BottomNavItem.Home.route) { HomeScreen(navController = navController) }
            composable(BottomNavItem.Map.route) { MapScreen(uid.toString()) }
            composable(BottomNavItem.Post.route) { PostScreen(uid.toString()) }
            composable(BottomNavItem.Challenges.route) { ChallengesScreen(uid.toString()) }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    uid
                )
            }
            composable(
                route = "other_users/{uid}",
                arguments = listOf(navArgument("uid") { type = NavType.StringType })
            ) { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("uid")
                if (uid != null) {
                    ProfileScreen(navController = navController, uid = uid)
                }
            }
            composable("friends_screen/{uid}") { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("uid")
                if (uid != null) {
                    FriendsListScreen(navController = navController, uid = uid)
                }
            }

        }
    }
}
