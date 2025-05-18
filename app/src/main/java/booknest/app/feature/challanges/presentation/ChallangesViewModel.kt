package booknest.app.feature.challanges.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import booknest.app.feature.challanges.data.Challenge
import booknest.app.feature.challanges.data.ChallengeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val repository: ChallengeRepository
) : ViewModel() {

    private val _challenges = MutableStateFlow<List<Challenge>>(emptyList())
    val challenges: StateFlow<List<Challenge>> = _challenges

    private val _activeChallenges = MutableStateFlow<List<Challenge>>(emptyList())
    val activeChallenges: StateFlow<List<Challenge>> = _activeChallenges

    fun loadChallenges(userId: String) {
        viewModelScope.launch {
            val available = repository.getAvailableChallengesForUser(userId)
            val active = repository.getUserActiveChallenges(userId)

            _challenges.value = available
            _activeChallenges.value = active
        }
    }


    fun startChallenge(userId: String, challengeId: String) {
        viewModelScope.launch {
            repository.startChallengeForUser(userId, challengeId.toString())
            // Reload after starting challenge
            loadChallenges(userId)
        }
    }
}