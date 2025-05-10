package booknest.app.feature.post.presentation

import androidx.lifecycle.ViewModel
import booknest.app.feature.post.data.Post
import booknest.app.feature.post.data.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import booknest.app.feature.post.data.PostUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class PostItemViewModel @Inject constructor(
    private val postRepository: PostRepository) : ViewModel() {

    private val _posts = MutableStateFlow<List<PostUiState>>(emptyList())
    val posts: StateFlow<List<PostUiState>> = _posts


    fun toggleLike(userId: String, post: Post) {
        viewModelScope.launch {
            postRepository.toggleLike(post.uid ?: "", userId)

            // Refresh the like state for this post
            val isLikedNow = postRepository.isPostLikedByUser(post.uid ?: "", userId)
            val likeCountNow = postRepository.getLikesCount(post.uid ?: "")

            _posts.value = _posts.value.map { postUiState ->
                if (postUiState.post.uid == post.uid) {
                    postUiState.copy(
                        isLiked = isLikedNow,
                        likeCount = likeCountNow
                    )
                } else {
                    postUiState
                }
            }
        }
    }


    fun fetchFriendsPosts(userId: String) {
        viewModelScope.launch {
            val posts = postRepository.getFriendsPosts(userId)

            val enrichedPosts = posts.map { post ->
                // Refresh the like state for this post
                val isLiked = postRepository.isPostLikedByUser(post.uid ?: "", userId)
                val likeCount = postRepository.getLikesCount(post.uid ?: "")

                PostUiState(
                    post = post,
                    isLiked = isLiked,
                    likeCount = likeCount
                )
            }

            _posts.value = enrichedPosts
        }
    }
}