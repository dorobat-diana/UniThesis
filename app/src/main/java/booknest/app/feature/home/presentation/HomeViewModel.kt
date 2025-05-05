package booknest.app.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import booknest.app.feature.home.data.HomeRepository
import booknest.app.feature.profil.data.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<UserProfile>>(emptyList())
    val users: StateFlow<List<UserProfile>> = _users

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

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