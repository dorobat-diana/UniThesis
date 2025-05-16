package booknest.app.feature.map.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import booknest.app.feature.post.data.Attraction
import booknest.app.feature.post.data.AttractionRepository
import booknest.app.feature.profil.data.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repo: AttractionRepository
) : ViewModel() {

    private val _attractions = MutableStateFlow<List<Attraction>>(emptyList())
    val attractions: StateFlow<List<Attraction>> = _attractions

    private val _selectedAttraction = MutableStateFlow<Attraction?>(null)
    val selectedAttraction: StateFlow<Attraction?> = _selectedAttraction

    fun loadVisitedAttractions(userid: String) {
        viewModelScope.launch {
            Log.d("MapViewModel", "Loading visited attractions for userId: $userid")
            val userProfile = repo.getUserProfileByUid(userid)
            if (userProfile == null) {
                Log.w("MapViewModel", "UserProfile is null for userId: $userid")
            } else {
                Log.d("MapViewModel", "UserProfile loaded: $userProfile")
            }
            val visitedAttractions = userProfile?.visitedAttractions ?: emptyList()
            Log.d("MapViewModel", "Visited attractions list: $visitedAttractions")
            val result = repo.getVisitedAttractionsByName(visitedAttractions)
            Log.d("MapViewModel", "Attractions fetched: ${result.size}")
            _attractions.value = result
        }
    }


    fun selectAttraction(attraction: Attraction) {
        _selectedAttraction.value = attraction
    }
}
