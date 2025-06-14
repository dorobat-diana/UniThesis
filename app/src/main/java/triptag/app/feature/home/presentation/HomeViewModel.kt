package triptag.app.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import triptag.app.feature.home.data.HomeRepository
import triptag.app.feature.profil.data.ProfileRepository
import triptag.app.feature.profil.data.UserProfile
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

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

}