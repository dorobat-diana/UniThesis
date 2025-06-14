package triptag.app.feature.profil.data

import android.graphics.Bitmap
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
            val doc = firestore.collection("users").document(uid).get().await()
            val userProfile = doc.toObject(UserProfile::class.java)
            userProfile
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateProfile(profile: UserProfile) {
        try {
            firestore.collection("users").document(profile.uid.toString()).set(profile).await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun isFriend(currentUserUid: String, targetUserUid: String): Boolean {
        return try {
            val targetUserDoc = firestore.collection("users").document(targetUserUid).get().await()
            val targetProfile = targetUserDoc.toObject(UserProfile::class.java)
            targetProfile?.friends?.contains(currentUserUid) == true
        } catch (e: Exception) {
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
                    null
                }
            }

            friendProfiles
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun getDefaultProfilePictureUrl(): String {
        val storageRef = storage.reference.child("default.jpg")
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
                } catch (e: Exception) {
                    null
                }
            }

            val imageRef = storage.reference.child("profile_pictures/${UUID.randomUUID()}.jpg")

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            imageRef.putBytes(data).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()

            userDocRef.update("profilePictureUrl", downloadUrl).await()
            downloadUrl
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateUserLevel(userId: String) {
        val userRef = firestore.collection("users").document(userId)
        try {
            val doc = userRef.get().await()
            val userProfile = doc.toObject(UserProfile::class.java) ?: return

            val currentLevel = userProfile.level
            val postsCount = userProfile.postsCount
            val completedChallenges = userProfile.completedChallenges

            val totalPoints = postsCount + completedChallenges * 5
            val nextLevelRequirement = (currentLevel + 1) * 5

            if (totalPoints >= nextLevelRequirement) {
                val newLevel = currentLevel + 1
                userRef.update("level", newLevel).await()
            }
        } catch (e: Exception) {
        }
    }
}
