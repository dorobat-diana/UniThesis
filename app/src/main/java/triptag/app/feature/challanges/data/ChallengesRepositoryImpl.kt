package triptag.app.feature.challanges.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengesRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChallengeRepository {
    private val challengesCollection = firestore.collection("challenges")
    private val userChallengesCollection = firestore.collection("userChallenges")

    override suspend fun getAvailableChallengesForUser(userId: String): List<Challenge> {

        val userChallengesSnapshot = userChallengesCollection
            .whereEqualTo("userId", userId)
            .get()
            .await()

        val excludedChallengeIds = userChallengesSnapshot.documents.mapNotNull { doc ->
            doc.getString("activeChallengeId")
        }

        val allChallengesSnapshot = challengesCollection.get().await()

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
            val challengeId =
                userChallengeDoc.getString("activeChallengeId") ?: return@mapNotNull null
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
                doc.reference.delete().await()
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
                val currentFound =
                    userChallengeDoc.get("attractionsFound") as? List<String> ?: emptyList()
                if (!currentFound.contains(attractionName)) {
                    val updatedFound = currentFound + attractionName

                    val updates = mutableMapOf<String, Any>(
                        "attractionsFound" to updatedFound
                    )

                    if (updatedFound.toSet() == challenge.attractionsToFind.toSet()) {
                        updates["status"] = "FINISHED"
                        incrementUserLevel(userId, 1)
                        incrementUserChallenges(userId, 1)
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
            val challengeId =
                userChallengeDoc.getString("activeChallengeId") ?: return@mapNotNull null
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
            val challengeDoc = challengesCollection.document(challengeId).get().await()

            val challenge = challengeDoc.toObject<Challenge>()
            if (challenge == null) {
                throw Exception("Challenge not found")
            }

            val userChallenge = UserChallenge(
                userId = userId,
                activeChallengeId = challengeId,
                startedAt = Timestamp.now(),
                status = "IN_PROGRESS",
                attractionsFound = emptyList()
            )
            val newDocRef = userChallengesCollection.document()

            newDocRef.set(userChallenge).await()

        } catch (e: Exception) {
            throw e
        }
    }
}