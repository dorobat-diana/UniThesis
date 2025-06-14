package triptag.app.feature.post.presentation

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import triptag.app.feature.challanges.data.ChallengeRepository
import triptag.app.feature.post.data.Attraction
import triptag.app.feature.post.data.AttractionRepository
import triptag.app.feature.profil.data.ProfileRepository
import javax.inject.Inject

@HiltViewModel
class AttractionViewModel @Inject constructor(
    private val repo: AttractionRepository,
    private val challengeRepo: ChallengeRepository,
    private val userRepo: ProfileRepository
) : ViewModel() {

    private val _nearbyAttractions = mutableStateListOf<Attraction>()
    val nearbyAttractions: List<Attraction> = _nearbyAttractions

    private val _postCreationStatus = MutableStateFlow<Result<Unit>?>(null)
    val postCreationStatus: StateFlow<Result<Unit>?> = _postCreationStatus

    private val _selectedAttraction = mutableStateOf<String?>(null)
    val selectedAttraction: State<String?> = _selectedAttraction

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isCreatingPost = MutableStateFlow(false)
    val isCreatingPost: StateFlow<Boolean> = _isCreatingPost


    fun onAttractionSelected(name: String) {
        _selectedAttraction.value = name
    }


    fun loadNearbyAttractions(currentLat: Double, currentLng: Double) {
        repo.fetchNearbyAttractions(currentLat, currentLng) { results ->
            _isLoading.value = true
            _nearbyAttractions.clear()
            _nearbyAttractions.addAll(results)
            _isLoading.value = false
        }
    }

    fun createPost(userId: String, photoUri: Uri, context: Context) {
        val attractionId = _selectedAttraction.value ?: return
        viewModelScope.launch {
            _isCreatingPost.value = true
            val result = repo.createPost(attractionId, photoUri, context)
            if (result.isSuccess) {
                challengeRepo.handleChallengeProgress(userId, attractionId)
                userRepo.updateUserLevel(userId)
            }
            _postCreationStatus.value = result
            _isCreatingPost.value = false
        }
    }

    fun clearStatus() {
        _postCreationStatus.value = null
    }
}
