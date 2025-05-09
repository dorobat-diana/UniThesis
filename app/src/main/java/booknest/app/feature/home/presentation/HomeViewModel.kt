package booknest.app.feature.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import booknest.app.feature.home.data.HomeRepository
import booknest.app.feature.post.data.Post
import booknest.app.feature.profil.data.ProfileRepository
import booknest.app.feature.profil.data.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _users = MutableStateFlow<List<UserProfile>>(emptyList())
    val users: StateFlow<List<UserProfile>> = _users

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _userMap = MutableStateFlow<Map<String, UserProfile>>(emptyMap())
    val userMap: StateFlow<Map<String, UserProfile>> = _userMap

    suspend fun loadFriendMap(currentUserId: String) {
        val friends = profileRepository.getFriends(currentUserId)
        _userMap.value = friends.associateBy { it.uid.toString() }
    }


    fun searchUsers(query: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _users.value = homeRepository.searchUsers(query)
            } catch (e: Exception) {
                _error.value = "Failed to search users"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearSearchResults() {
        _users.value = emptyList()
    }

    fun fetchFriendsPosts(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _posts.value = homeRepository.getFriendsPosts(userId)
            } catch (e: Exception) {
                _error.value = "Failed to fetch posts: ${e.message}"
                Log.e("HomeViewModel", "Error fetching posts: ${e.message}", e)
            } finally {
                _loading.value = false
            }
        }
    }


}