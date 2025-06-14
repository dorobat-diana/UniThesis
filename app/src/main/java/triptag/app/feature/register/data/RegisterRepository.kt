package triptag.app.feature.register.data

import triptag.app.feature.profil.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class RegisterRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) {
    suspend fun registerUser(email: String, password: String): Result<String> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID not found")
            val username = "user_${UUID.randomUUID().toString().take(8)}"
            val defaultProfileUrl = getDefaultProfilePictureUrl()
            val userProfile = UserProfile(
                uid = userId,
                email = email,
                username = username,
                profilePictureUrl = defaultProfileUrl
            )

            firestore.collection("users").document(userId).set(userProfile).await()

            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getDefaultProfilePictureUrl(): String {
        val storageRef = firebaseStorage.reference.child("default.jpg")
        return storageRef.downloadUrl.await().toString()
    }
}
