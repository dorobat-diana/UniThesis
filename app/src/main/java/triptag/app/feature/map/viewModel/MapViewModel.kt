package triptag.app.feature.map.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import triptag.app.feature.post.data.Attraction
import triptag.app.feature.post.data.AttractionRepository
import javax.inject.Inject

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
            val userProfile = repo.getUserProfileByUid(userid)
            val visitedAttractions = userProfile?.visitedAttractions ?: emptyList()
            val result = repo.getVisitedAttractionsByName(visitedAttractions)
            _attractions.value = result
        }
    }

    fun selectAttraction(attraction: Attraction) {
        _selectedAttraction.value = attraction
    }
}
