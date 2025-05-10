package booknest.app.feature.post.data

import android.util.Log
import booknest.app.feature.profil.data.ProfileRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PostRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val profileRepository: ProfileRepository
) :PostRepository{

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
            Log.e("HomeRepositoryImpl", "Error checking like status: ${e.message}", e)
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
                Log.d("HomeRepositoryImpl", "Unliked post $postId by user $userId")
            } else {
                likeRef.set(mapOf("likedAt" to Timestamp.now())).await()
                Log.d("HomeRepositoryImpl", "Liked post $postId by user $userId")
            }
        } catch (e: Exception) {
            Log.e("HomeRepositoryImpl", "Error toggling like: ${e.message}", e)
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
            Log.e("HomeRepositoryImpl", "Error fetching like count: ${e.message}", e)
            0
        }
    }

    override suspend fun getFriendsPosts(userId: String): List<Post> {
        return try {
            val timeLimit = System.currentTimeMillis() - 48 * 60 * 60 * 1000
            Log.d("HomeRepositoryImpl", "Time limit (ms): $timeLimit, Date: ${java.util.Date(timeLimit)}")

            val friends = profileRepository.getFriends(userId)
            val friendIds = friends.mapNotNull { it.uid }
            Log.d("HomeRepositoryImpl", "Friend IDs: $friendIds")

            if (friendIds.isEmpty()) {
                Log.d("HomeRepositoryImpl", "No friends found for user with ID: $userId")
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

                Log.d("HomeRepositoryImpl", "Post timestamp (ms): $postTimestamp, Time limit (ms): $timeLimit")

                if (postTimestamp != null && postTimestamp > timeLimit) {
                    post
                } else {
                    null
                }
            }

            Log.d("HomeRepositoryImpl", "Fetched ${posts.size} posts for user with ID: $userId")
            posts
        } catch (e: Exception) {
            Log.e("HomeRepositoryImpl", "Error fetching posts: ${e.message}", e)
            emptyList()
        }
    }

}