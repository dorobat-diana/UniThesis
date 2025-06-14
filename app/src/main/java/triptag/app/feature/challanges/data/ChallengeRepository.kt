package triptag.app.feature.challanges.data

interface ChallengeRepository {
    suspend fun getAvailableChallengesForUser(userId: String): List<Challenge>
    suspend fun getUserActiveChallenges(userId: String): List<Challenge>
    suspend fun startChallengeForUser(userId: String, challengeId: String)
    suspend fun checkAndTerminateExpiredChallenges(userId: String)
    suspend fun handleChallengeProgress(userId: String, attractionName: String)
    suspend fun getUserFinishedChallenges(userId: String): List<Challenge>
    suspend fun getUserChallenges(userId: String): List<UserChallenge>
}
