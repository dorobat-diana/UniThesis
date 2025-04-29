package booknest.app.feature.profil.data


interface ProfileRepository {
    suspend fun getProfile(uid: String): UserProfile?
    suspend fun updateProfile(profile: UserProfile)
}