package booknest.app.feature.profil.presentation

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import booknest.app.feature.post.data.Post
import booknest.app.feature.profil.data.UserProfile
import booknest.app.feature.profil.data.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isFriend = MutableStateFlow<Boolean?>(null)
    val isFriend: StateFlow<Boolean?> = _isFriend

    fun checkIfFriend(currentUserUid: String, targetUserUid: String) {
        viewModelScope.launch {
            val result = repository.isFriend(currentUserUid, targetUserUid)
            _isFriend.value = result
        }
    }

    fun loadUser(uid: String) {
        viewModelScope.launch {
            try {
                Log.d("ProfileViewModel", "Loading user profile for UID: $uid")
                val userProfile = repository.getProfile(uid)
                Log.d("ProfileViewModel", "Fetched user: $userProfile")
                _profile.value = userProfile
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Failed to load user profile: ${e.message}", e)
                _error.value = "Failed to load user profile"
            }
        }
    }

    fun updateProfile(username: String, caption: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _profile.value?.let {
                    val updated = it.copy(username = username, caption = caption)
                    repository.updateProfile(updated)
                    _profile.value = updated
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Failed to update profile", e)
                _error.value = "Failed to update profile"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateProfilePicture(imageUrl: String) {
        viewModelScope.launch {
            try {
                _profile.value?.let {
                    val updated = it.copy(profilePictureUrl = imageUrl)
                    repository.updateProfile(updated)
                    _profile.value = updated
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Failed to update profile picture", e)
                _error.value = "Failed to update profile picture"
            }
        }
    }

    fun addFriend(currentUserUid: String, targetUserUid: String) {
        viewModelScope.launch {
            try {
                repository.addFriend(currentUserUid, targetUserUid)
                _isFriend.value = true
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Failed to add friend", e)
                _error.value = "Failed to add friend"
            }
        }
    }

    fun removeFriend(currentUserUid: String, targetUserUid: String) {
        viewModelScope.launch {
            try {
                repository.removeFriend(currentUserUid, targetUserUid)
                _isFriend.value = false
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Failed to remove friend", e)
                _error.value = "Failed to remove friend"
            }
        }
    }

    fun updateProfilePictureFromBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            val imageUrl = repository.uploadImageToStorage(bitmap)
            if (imageUrl != null) {
                updateProfilePicture(imageUrl)
            } else {
                _error.value = "Failed to upload image"
            }
        }
    }

}
