package booknest.app.feature.challanges.data

interface ChallengeRepository {
    suspend fun getAvailableChallengesForUser(userId: String): List<Challenge>
    suspend fun getUserActiveChallenges(userId: String): List<Challenge>
    suspend fun startChallengeForUser(userId: String, challengeId: String)
}