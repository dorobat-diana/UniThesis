package booknest.app.feature.post.data

data class Post(
    val uid: String ? = null,
    val userId: String ? = null,
    val attraction: String ? = null,
    val photoUrl: String ? = null,
    val likes: List<String> = emptyList()
)

