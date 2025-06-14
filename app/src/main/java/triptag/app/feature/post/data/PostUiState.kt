package triptag.app.feature.post.data

data class PostUiState(
    val post: Post,
    val isLiked: Boolean,
    val likeCount: Int
)
