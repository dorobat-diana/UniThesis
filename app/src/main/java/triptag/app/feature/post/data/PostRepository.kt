package triptag.app.feature.post.data

interface PostRepository {
    suspend fun isPostLikedByUser(postId: String, userId: String): Boolean
    suspend fun toggleLike(postId: String, userId: String)
    suspend fun getLikesCount(postId: String): Int
    suspend fun getFriendsPosts(userId: String): List<Post>
    suspend fun loadUserPosts(userId: String): List<Post>
}