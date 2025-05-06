package booknest.app.feature.profil.data

data class UserProfile(
    val uid: String? = null,
    val email: String? = null,
    val username: String? = null,
    val profilePictureUrl: String? = null,
    val caption: String = "",
    val friendsCount: Int = 0,
    val postsCount: Int = 0,
    val friends: List<String> = emptyList()
) {
    constructor() : this(null, null, null, null, "", 0, 0)
}
