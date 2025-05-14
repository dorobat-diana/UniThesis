package booknest.app.feature.post.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import booknest.app.feature.post.data.Attraction
import booknest.app.feature.post.data.AttractionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.State
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttractionViewModel @Inject constructor(
    private val repo: AttractionRepository
) : ViewModel() {

    private val _nearbyAttractions = mutableStateListOf<Attraction>()
    val nearbyAttractions: List<Attraction> = _nearbyAttractions

    private val _postCreationStatus = MutableStateFlow<Result<Unit>?>(null)
    val postCreationStatus: StateFlow<Result<Unit>?> = _postCreationStatus

    private val _selectedAttraction = mutableStateOf<String?>(null)
    val selectedAttraction: State<String?> = _selectedAttraction

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


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

    fun createPost(photoUri: Uri,context: Context) {
        val attractionId = _selectedAttraction.value ?: return
        Log.d("CreatePost", "selected class: ${attractionId}")
        viewModelScope.launch {
            val result = repo.createPost(attractionId, photoUri,context )
            Log.d("CreatePost", "post created")
            _postCreationStatus.value = result
        }
    }

    fun clearStatus() {
        _postCreationStatus.value = null
    }
}
