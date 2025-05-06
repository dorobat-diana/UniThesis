package booknest.app.feature.post.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import booknest.app.feature.post.data.Attraction
import booknest.app.feature.post.data.AttractionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repo: AttractionRepository
) : ViewModel() {

    private val _nearbyAttractions = mutableStateListOf<Attraction>()
    val nearbyAttractions: List<Attraction> = _nearbyAttractions

    fun loadNearbyAttractions(currentLat: Double, currentLng: Double) {
        repo.fetchNearbyAttractions(currentLat, currentLng) { results ->
            _nearbyAttractions.clear()
            _nearbyAttractions.addAll(results)
        }
    }
}
