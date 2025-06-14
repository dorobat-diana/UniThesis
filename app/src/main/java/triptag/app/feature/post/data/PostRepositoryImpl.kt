package triptag.app.feature.post.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import triptag.app.feature.profil.data.ProfileRepository

class PostRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val profileRepository: ProfileRepository
) : PostRepository {

    override suspend fun isPostLikedByUser(postId: String, userId: String): Boolean {
        return try {
            val likeDoc = firestore.collection("posts")
                .document(postId)
                .collection("likes")
                .document(userId)
                .get()
                .await()
            likeDoc.exists()
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun toggleLike(postId: String, userId: String) {
        try {
            val likeRef = firestore.collection("posts")
                .document(postId)
                .collection("likes")
                .document(userId)

            val likeSnapshot = likeRef.get().await()
            if (likeSnapshot.exists()) {
                likeRef.delete().await()
            } else {
                likeRef.set(mapOf("likedAt" to Timestamp.now())).await()
            }
        } catch (e: Exception) {
        }
    }

    override suspend fun getLikesCount(postId: String): Int {
        return try {
            val snapshot = firestore.collection("posts")
                .document(postId)
                .collection("likes")
                .get()
                .await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun getFriendsPosts(userId: String): List<Post> {
        return try {
            val timeLimit = System.currentTimeMillis() - 48 * 60 * 60 * 1000

            val friends = profileRepository.getFriends(userId)
            val friendIds = friends.mapNotNull { it.uid }

            if (friendIds.isEmpty()) {
                return emptyList()
            }

            val postsSnapshot = firestore.collection("posts")
                .whereIn("userId", friendIds)
                .whereGreaterThan("timestamp", Timestamp(timeLimit / 1000, 0))
                .get()
                .await()

            val posts = postsSnapshot.documents.mapNotNull { doc ->
                val post = doc.toObject(Post::class.java)
                val postTimestamp = post?.timestamp?.toDate()?.time

                if (postTimestamp != null && postTimestamp > timeLimit) {
                    post
                } else {
                    null
                }
            }
            posts
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun loadUserPosts(userId: String): List<Post> {
        return try {
            val postsSnapshot = firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val posts = postsSnapshot.documents.mapNotNull { document ->
                document.toObject(Post::class.java)
            }
            return posts
        } catch (e: Exception) {
            emptyList()
        }
    }
}
