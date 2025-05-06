package booknest.app.feature.profil.data

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID

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
                batch.update(userRef, "friendsCount", FieldValue.increment(1))
                batch.update(targetRef, "friendsCount", FieldValue.increment(1))
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
                batch.update(userRef, "friendsCount", FieldValue.increment(-1))
                batch.update(targetRef, "friendsCount", FieldValue.increment(-1))
            }.await()
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error removing friend", e)
            throw e
        }
    }

    override suspend fun getFriends(uid: String): List<UserProfile> {
        return try {
            val userDoc = firestore.collection("users").document(uid).get().await()
            val userProfile = userDoc.toObject(UserProfile::class.java)

            val friendUids = userProfile?.friends ?: emptyList()

            if (friendUids.isEmpty()) return emptyList()

            val friendProfiles = mutableListOf<UserProfile>()

            friendUids.forEach { friendUid ->
                try {
                    val friendDoc = firestore.collection("users").document(friendUid).get().await()
                    friendDoc.toObject(UserProfile::class.java)?.let {
                        friendProfiles.add(it)
                    }
                } catch (e: Exception) {
                    Log.w("ProfileRepository", "Error fetching friend $friendUid: ${e.message}")
                }
            }

            friendProfiles
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error fetching friends list: ${e.message}", e)
            emptyList()
        }
    }

    private suspend fun getDefaultProfilePictureUrl(): String {
        val storageRef = storage.reference.child("default.jpg")
        Log.d("RegisterRepository", "Fetched default profile picture URL: $storageRef")
        return storageRef.downloadUrl.await().toString()
    }

    override suspend fun uploadImageToStorage(bitmap: Bitmap): String? {
        val currentUser = auth.currentUser ?: return null
        val uid = currentUser.uid
        val userDocRef = firestore.collection("users").document(uid)

        return try {
            val docSnapshot = userDocRef.get().await()
            val userProfile = docSnapshot.toObject(UserProfile::class.java)
            val oldImageUrl = userProfile?.profilePictureUrl
            val defaultImageUrl = getDefaultProfilePictureUrl()

            if (!oldImageUrl.isNullOrEmpty() && oldImageUrl != defaultImageUrl) {
                try {
                    storage.getReferenceFromUrl(oldImageUrl).delete().await()
                    Log.d("ProfileRepository", "Old profile image deleted.")
                } catch (e: Exception) {
                    Log.w("ProfileRepository", "Failed to delete old image: ${e.message}")
                }
            }

            val imageRef = storage.reference.child("profile_pictures/${UUID.randomUUID()}.jpg")

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            imageRef.putBytes(data).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()

            userDocRef.update("profilePictureUrl", downloadUrl).await()
            Log.d("ProfileRepository", "Profile picture updated.")
            downloadUrl
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error uploading image: ${e.message}", e)
            null
        }
    }

}