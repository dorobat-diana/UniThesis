package booknest.app.feature.challanges.data

import com.google.firebase.Timestamp

data class UserChallenge(
    val userId: String,
    val activeChallengeId: String,
    val attractionsFound: List<String> = emptyList(),
    val startedAt: Timestamp,
    val status: String
)