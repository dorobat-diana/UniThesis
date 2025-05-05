package booknest.app.feature.profil.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ProfileRepository {

    override suspend fun getProfile(uid: String): UserProfile? {
        return try {
            Log.d("ProfileRepository", "Fetching user profile for UID: $uid")
            val doc = firestore.collection("users").document(uid).get().await()
            val userProfile = doc.toObject(UserProfile::class.java)
            Log.d("ProfileRepository", "Fetched user: $userProfile")
            userProfile
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error fetching user profile: ${e.message}", e)
            null
        }
    }

    override suspend fun updateProfile(profile: UserProfile) {
        try {
            firestore.collection("users").document(profile.uid.toString()).set(profile).await()
            Log.d("ProfileRepository", "Successfully updated profile for ${profile.uid}")
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Failed to update profile: ${e.message}", e)
            throw e
        }
    }

    override suspend fun isFriend(currentUserUid: String, targetUserUid: String): Boolean {
        return try {
            val targetUserDoc = firestore.collection("users").document(targetUserUid).get().await()
            val targetProfile = targetUserDoc.toObject(UserProfile::class.java)
            targetProfile?.friends?.contains(currentUserUid) == true
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error checking friendship: ${e.message}", e)
            false
        }
    }

    override suspend fun addFriend(currentUserUid: String, targetUserUid: String) {
        try {
            val userRef = firestore.collection("users").document(currentUserUid)
            val targetRef = firestore.collection("users").document(targetUserUid)

            firestore.runBatch { batch ->
                batch.update(userRef, "friends", FieldValue.arrayUnion(targetUserUid))
                batch.update(targetRef, "friends", FieldValue.arrayUnion(currentUserUid))
            }.await()
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error adding friend", e)
            throw e
        }
    }

    override suspend fun removeFriend(currentUserUid: String, targetUserUid: String) {
        try {
            val userRef = firestore.collection("users").document(currentUserUid)
            val targetRef = firestore.collection("users").document(targetUserUid)

            firestore.runBatch { batch ->
                batch.update(userRef, "friends", FieldValue.arrayRemove(targetUserUid))
                batch.update(targetRef, "friends", FieldValue.arrayRemove(currentUserUid))
            }.await()
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error removing friend", e)
            throw e
        }
    }


}