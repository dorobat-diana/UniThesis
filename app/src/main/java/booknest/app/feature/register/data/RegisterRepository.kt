package booknest.app.feature.register.data

import android.util.Log
import booknest.app.feature.profil.data.UserProfile
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
            Log.d("RegisterRepository", "Attempting to register with email: $email and password: $password")
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID not found")
            // Generate default username
            val username = "user_${UUID.randomUUID().toString().take(8)}"

            // Use a default profile picture from assets or storage
            val defaultProfileUrl = getDefaultProfilePictureUrl()

            val userProfile = UserProfile(
                uid = userId,
                email = email,
                username = username,
                profilePictureUrl = defaultProfileUrl
            )

            Log.d("RegisterRepository", "User created: UID = $userId")
            Log.d("RegisterRepository", "Username = $username")
            Log.d("RegisterRepository", "Default Profile Picture URL = $defaultProfileUrl")

            firestore.collection("users").document(userId).set(userProfile).await()

            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getDefaultProfilePictureUrl(): String {
        val storageRef = firebaseStorage.reference.child("default.jpg")
        Log.d("RegisterRepository", "Fetched default profile picture URL: $storageRef")
        return storageRef.downloadUrl.await().toString()
    }
}