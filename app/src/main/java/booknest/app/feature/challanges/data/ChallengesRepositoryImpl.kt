package booknest.app.feature.challanges.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengesRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChallengeRepository {
    private val challengesCollection = firestore.collection("challenges")
    private val userChallengesCollection = firestore.collection("userChallenges")

    override suspend fun getAvailableChallengesForUser(userId: String): List<Challenge> {
        // Step 1: Get all user-challenge mappings for this user
        val userChallengesSnapshot = userChallengesCollection
            .whereEqualTo("userId", userId)
            .get()
            .await()

        // Step 2: Collect all activeChallengeIds for this user (no need to check status)
        val excludedChallengeIds = userChallengesSnapshot.documents.mapNotNull { doc ->
            doc.getString("activeChallengeId")
        }

        // Step 3: Fetch all challenges
        val allChallengesSnapshot = challengesCollection.get().await()

        // Step 4: Filter out any challenges already associated with the user
        return allChallengesSnapshot.documents.mapNotNull { doc ->
            val challenge = doc.toObject<Challenge>()?.copy(id = doc.id)
            if (challenge != null && !excludedChallengeIds.contains(challenge.id)) {
                challenge
            } else null
        }
    }




    override suspend fun getUserActiveChallenges(userId: String): List<Challenge> {
        val userChallengesSnapshot = userChallengesCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", "IN_PROGRESS")
            .get()
            .await()

        if (userChallengesSnapshot.isEmpty) {
            return emptyList()
        }
        return userChallengesSnapshot.documents.mapNotNull { userChallengeDoc ->
            val challengeId = userChallengeDoc.getString("activeChallengeId") ?: return@mapNotNull null
            val challengeSnapshot = challengesCollection.document(challengeId).get().await()
            challengeSnapshot.toObject<Challenge>()?.copy(id = challengeSnapshot.id)
        }
    }

    override suspend fun checkAndTerminateExpiredChallenges(userId: String) {
        val userChallengesSnapshot = userChallengesCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", "IN_PROGRESS")
            .get()
            .await()

        for (doc in userChallengesSnapshot.documents) {
            val challengeId = doc.getString("activeChallengeId") ?: continue
            val challengeDoc = challengesCollection.document(challengeId).get().await()
            val challenge = challengeDoc.toObject<Challenge>() ?: continue

            val startedAt = doc.getTimestamp("startedAt") ?: continue
            val deadlineMillis = startedAt.toDate().time + challenge.timeLimit * 24 * 60 * 60 * 1000
            val deadline = Timestamp(deadlineMillis / 1000, 0)

            val now = Timestamp.now()
            if (now > deadline) {
                doc.reference.delete().await()  // ⬅ Delete the document instead of updating status
            }
        }
    }

    override suspend fun handleChallengeProgress(userId: String, attractionName: String) {
        val userChallengesSnapshot = userChallengesCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", "IN_PROGRESS")
            .get()
            .await()

        for (userChallengeDoc in userChallengesSnapshot.documents) {
            val challengeId = userChallengeDoc.getString("activeChallengeId") ?: continue
            val challengeDoc = challengesCollection.document(challengeId).get().await()
            val challenge = challengeDoc.toObject<Challenge>() ?: continue

            if (challenge.attractionsToFind.contains(attractionName)) {
                val currentFound = userChallengeDoc.get("attractionsFound") as? List<String> ?: emptyList()
                if (!currentFound.contains(attractionName)) {
                    val updatedFound = currentFound + attractionName

                    val updates = mutableMapOf<String, Any>(
                        "attractionsFound" to updatedFound
                    )

                    if (updatedFound.toSet() == challenge.attractionsToFind.toSet()) {
                        updates["status"] = "FINISHED"
                        incrementUserLevel(userId, 1)
                        incrementUserChallenges(userId,1)
                    }

                    userChallengeDoc.reference.update(updates).await()
                }
            }
        }
    }

    private suspend fun incrementUserLevel(userId: String, incrementBy: Int) {
        val userRef = firestore.collection("users").document(userId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentLevel = snapshot.getLong("level") ?: 0
            transaction.update(userRef, "level", currentLevel + incrementBy)
        }.await()
    }

    private suspend fun incrementUserChallenges(userId: String, incrementBy: Int) {
        val userRef = firestore.collection("users").document(userId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentLevel = snapshot.getLong("completedChallenges") ?: 0
            transaction.update(userRef, "completedChallenges", currentLevel + incrementBy)
        }.await()
    }

    override suspend fun getUserFinishedChallenges(userId: String): List<Challenge> {
        val userChallengesSnapshot = userChallengesCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", "FINISHED")
            .get()
            .await()

        if (userChallengesSnapshot.isEmpty) {
            return emptyList()
        }

        return userChallengesSnapshot.documents.mapNotNull { userChallengeDoc ->
            val challengeId = userChallengeDoc.getString("activeChallengeId") ?: return@mapNotNull null
            val challengeSnapshot = challengesCollection.document(challengeId).get().await()
            challengeSnapshot.toObject<Challenge>()?.copy(id = challengeSnapshot.id)
        }
    }

    override suspend fun getUserChallenges(userId: String): List<UserChallenge> {
        val snapshot = userChallengesCollection
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject<UserChallenge>()
        }
    }

    override suspend fun startChallengeForUser(userId: String, challengeId: String) {
        try {
            Log.d("ChallengeRepo", "startChallengeForUser called with userId: $userId, challengeId: $challengeId")

            // Log the challenges collection path
            Log.d("ChallengeRepo", "Challenges collection path: ${challengesCollection.path}")
            Log.d("ChallengeRepo", "UserChallenges collection path: ${userChallengesCollection.path}")

            // Attempt to fetch the challenge document
            Log.d("ChallengeRepo", "Fetching challenge document with ID: $challengeId")
            val challengeDoc = challengesCollection.document(challengeId).get().await()
            Log.d("ChallengeRepo", "Challenge document fetched: exists=${challengeDoc.exists()}")

            val challenge = challengeDoc.toObject<Challenge>()
            if (challenge == null) {
                Log.e("ChallengeRepo", "Challenge document is null for ID: $challengeId")
                throw Exception("Challenge not found")
            }
            Log.d("ChallengeRepo", "Challenge object created: $challenge")

            val userChallenge = UserChallenge(
                userId = userId,
                activeChallengeId = challengeId,
                startedAt = Timestamp.now(),
                status = "IN_PROGRESS",
                attractionsFound = emptyList()
            )
            Log.d("ChallengeRepo", "UserChallenge object created: $userChallenge")

            // Create new doc with random ID in userChallenges collection
            Log.d("ChallengeRepo", "Creating new document in userChallenges collection")
            val newDocRef = userChallengesCollection.document()
            Log.d("ChallengeRepo", "New user challenge document ID: ${newDocRef.id}")

            // Save user challenge
            newDocRef.set(userChallenge).await()
            Log.d("ChallengeRepo", "User challenge saved successfully")

        } catch (e: Exception) {
            Log.e("ChallengeRepo", "Error starting challenge for user: ${e.message}", e)
            throw e
        }
    }
}