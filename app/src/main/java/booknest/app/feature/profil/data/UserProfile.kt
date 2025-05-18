package booknest.app.feature.profil.data

import booknest.app.feature.post.data.Attraction

data class UserProfile(
    val uid: String? = null,
    val email: String? = null,
    val username: String? = null,
    val profilePictureUrl: String? = null,
    val caption: String = "",
    val friendsCount: Int = 0,
    val postsCount: Int = 0,
    val friends: List<String> = emptyList(),
    val visitedAttractions: List<String> = emptyList(),
    val level: Int = 1,
    val completedChallenges:Int = 0
) {
    constructor() : this(null, null, null, null, "", 0, 0, emptyList(), emptyList(), 1, 0)
}
