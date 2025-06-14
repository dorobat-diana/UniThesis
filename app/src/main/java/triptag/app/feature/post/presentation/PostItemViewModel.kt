package triptag.app.feature.post.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import triptag.app.feature.post.data.Post
import triptag.app.feature.post.data.PostRepository
import triptag.app.feature.post.data.PostUiState
import javax.inject.Inject

@HiltViewModel
class PostItemViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<PostUiState>>(emptyList())
    val posts: StateFlow<List<PostUiState>> = _posts

    private val _postsUser = MutableStateFlow<List<PostUiState>>(emptyList())
    val postsUser: StateFlow<List<PostUiState>> = _postsUser

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun toggleLike(userId: String, post: Post) {
        viewModelScope.launch {
            postRepository.toggleLike(post.uid ?: "", userId)

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

    fun fetchUserPosts(userId: String) {
        viewModelScope.launch {
            val posts = postRepository.loadUserPosts(userId)

            val enrichedPosts = posts.map { post ->
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
