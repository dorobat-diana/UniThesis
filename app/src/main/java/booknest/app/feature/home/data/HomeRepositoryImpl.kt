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
}