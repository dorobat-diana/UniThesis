package triptag.app.feature.home.data

import triptag.app.feature.profil.data.UserProfile

interface HomeRepository {
    suspend fun searchUsers(query: String): List<UserProfile>
}
