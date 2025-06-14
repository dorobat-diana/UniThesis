package triptag.app.feature.profil.data

import android.graphics.Bitmap

interface ProfileRepository {
    suspend fun getProfile(uid: String): UserProfile?
    suspend fun updateProfile(profile: UserProfile)
    suspend fun isFriend(currentUserUid: String, targetUserUid: String): Boolean
    suspend fun addFriend(currentUserUid: String, targetUserUid: String)
    suspend fun removeFriend(currentUserUid: String, targetUserUid: String)
    suspend fun getFriends(uid: String): List<UserProfile>
    suspend fun uploadImageToStorage(bitmap: Bitmap): String?
    suspend fun updateUserLevel(userId: String)
}
