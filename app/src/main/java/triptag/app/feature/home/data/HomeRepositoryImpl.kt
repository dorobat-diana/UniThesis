package triptag.app.feature.home.data

import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.tasks.await
import triptag.app.feature.profil.data.ProfileRepository
import triptag.app.feature.profil.data.UserProfile

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val profileRepository: ProfileRepository,
) : HomeRepository {

    override suspend fun searchUsers(query: String): List<UserProfile> {
        return try {
            val usersSnapshot = firestore.collection("users")
                .orderBy("username")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()

            val users = usersSnapshot.documents.mapNotNull { doc ->
                doc.toObject(UserProfile::class.java)
            }
            users
        } catch (e: Exception) {
            emptyList()
        }
    }
}