package booknest.app.feature.challanges.data

import booknest.app.feature.profil.data.UserProfile

data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val attractionsToFind: List<String>,
    val timeLimit: Int // time allowed for challenge in days
) {
    constructor() : this("","","",emptyList<String>(),0)
}