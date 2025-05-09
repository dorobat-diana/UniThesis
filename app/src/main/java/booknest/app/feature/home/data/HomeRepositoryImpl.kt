package booknest.app.feature.home.data

import android.util.Log
import booknest.app.feature.post.data.Post
import booknest.app.feature.profil.data.ProfileRepository
import booknest.app.feature.profil.data.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val profileRepository: ProfileRepository,
) : HomeRepository {

    override suspend fun searchUsers(query: String): List<UserProfile> {
        return try {
            Log.d("HomeRepository", "Searching for users with query: $query")
            val usersSnapshot = firestore.collection("users")
                .orderBy("username")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()

            val users = usersSnapshot.documents.mapNotNull { doc ->
                doc.toObject(UserProfile::class.java)
            }
            Log.d("HomeRepository", "Found users: $users")
            users
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error searching for users: ${e.message}", e)
            emptyList()
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