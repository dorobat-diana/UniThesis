package booknest.app.feature.home.data

import booknest.app.feature.profil.data.UserProfile

interface HomeRepository {
    suspend fun searchUsers(query: String): List<UserProfile>
}